#include <stdio.h>
#include <elf.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>



int main(int argc, char** argv)
{
  int fd;
  Elf32_Ehdr ehdr;
  Elf32_Shdr shdr;
  char * section_name_table;
  int i;
  unsigned int base, length;
  char *content;
  unsigned short nsize;
  //参数验证
  if(argc != 3)
  {
    printf("Encrypt section of elf file\n\nUsage:\n\t%s <elf_file> <section_name>\n", *argv);
    goto _error;
  }

  if((fd = open(argv[1], O_RDWR, 0777)) == -1)
  {
    perror("open");
    goto _error;
  }

  if(read(fd, &ehdr, sizeof(Elf32_Ehdr)) != sizeof(Elf32_Ehdr))
  {
    perror("read elf header");
    goto _error;
  }

  //读取节头字符串表
  printf("[+] Begining find section %s\n", argv[2]);
  lseek(fd, ehdr.e_shoff+sizeof(Elf32_Shdr)*ehdr.e_shstrndx, SEEK_SET);
  if(read(fd, &shdr, sizeof(Elf32_Shdr)) != sizeof(Elf32_Shdr))
  {
    perror("read elf section header which contain string table");
    goto _error;
  }

  if((section_name_table = (char*) malloc(shdr.sh_size)) == NULL)
  {
    perror("malloc for SHT_STRTAB");
    goto _error;
  }

  lseek(fd, shdr.sh_offset, SEEK_SET);
  if(read(fd, section_name_table, shdr.sh_size) != shdr.sh_size)
  {
    perror("read string table");
    goto _error;
  }
  lseek(fd, ehdr.e_shoff, SEEK_SET);
  //根据节头名来定位需要加密的节头
  for(i=0; i<ehdr.e_shnum; i++)
  {
    if(read(fd, &shdr, sizeof(Elf32_Shdr)) != sizeof(Elf32_Shdr))
    {
      perror("read section");
      goto _error;
    }
    if(strcmp(section_name_table+shdr.sh_name, argv[2]) == 0)
    {
      base = shdr.sh_offset;
      length = shdr.sh_size;
      printf("[+] Find section %s\n", argv[2]);
      printf("[+] %s section offset is %X\n", argv[2], base);
      printf("[+] %s section size is %d\n", argv[2], length);
      break;
    }
  }

  //开始加密逻辑
  lseek(fd, base, SEEK_SET);
  content = (char *)malloc(length);
  if(content == NULL)
  {
    perror("malloc space for section");
    goto _error;
  }
  if(read(fd, content, length) != length)
  {
    perror("read section in encrpt");
    goto _error;
  }

  //将该节具体长度值替换到入口点去
  //将该节的偏移地址替换到文件头中的节头表偏移值中去
  nsize = length/4096 + (length%4096 == 0 ? 0 : 1);
  ehdr.e_entry = (length << 16) + nsize;
  ehdr.e_shoff = base;
  printf("[+] %s section use %d memory page!\n", argv[2], nsize);


  //取反加密
  for(i=0; i<length; i++)
  {
    content[i] = ~content[i];
  }

  lseek(fd, 0, SEEK_SET);
  if(write(fd, &ehdr, sizeof(Elf32_Ehdr)) != sizeof(Elf32_Ehdr))
  {
    perror("write ELF header to file");
    goto _error;
  }
  lseek(fd, base, SEEK_SET);
  if(write(fd, content, length) != length)
  {
    perror("write encrypted section to file");
    goto _error;
  }

  printf("[+] Encrypt section %s completed!\n", argv[2]);
  _error:
  free(section_name_table);
  free(content);
  close(fd);
  return 0;
}
