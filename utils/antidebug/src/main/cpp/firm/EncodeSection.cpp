#include <stdio.h>
#include <fcntl.h>
#include "elf.h"
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main(int argc, char** argv){
    char *encodeSoName = "libdemo.so";
    char target_section[] = ".mytext";
    char *shstr = NULL;
    char *content = NULL;
    Elf32_Ehdr ehdr;
    Elf32_Shdr shdr;
    int i;
    unsigned int base, length;
    unsigned short nblock;
    unsigned short nsize;
    unsigned char block_size = 16;

    int fd;

    fd = open(encodeSoName, O_RDWR);
    if(fd < 0){
        printf("open %s failed\n", argv[1]);
        goto _error;
    }

    if(read(fd, &ehdr, sizeof(Elf32_Ehdr)) != sizeof(Elf32_Ehdr)){
        puts("Read ELF header error");
        goto _error;
    }

    lseek(fd, ehdr.e_shoff + sizeof(Elf32_Shdr) * ehdr.e_shstrndx, SEEK_SET);

    if(read(fd, &shdr, sizeof(Elf32_Shdr)) != sizeof(Elf32_Shdr)){
        puts("Read ELF section string table error");
        goto _error;
    }

    if((shstr = (char *) malloc(shdr.sh_size)) == NULL){
        puts("Malloc space for section string table failed");
        goto _error;
    }

    lseek(fd, shdr.sh_offset, SEEK_SET);
    if(read(fd, shstr, shdr.sh_size) != shdr.sh_size){
        puts("Read string table failed");
        goto _error;
    }

    lseek(fd, ehdr.e_shoff, SEEK_SET);
    for(i = 0; i < ehdr.e_shnum; i++){
        if(read(fd, &shdr, sizeof(Elf32_Shdr)) != sizeof(Elf32_Shdr)){
            puts("Find section .text procedure failed");
            goto _error;
        }
        if(strcmp(shstr + shdr.sh_name, target_section) == 0){
            base = shdr.sh_offset;
            length = shdr.sh_size;
            printf("Find section %s\n", target_section);
            break;
        }
    }

    lseek(fd, base, SEEK_SET);
    content = (char*) malloc(length);
    if(content == NULL){
        puts("Malloc space for content failed");
        goto _error;
    }
    if(read(fd, content, length) != length){
        puts("Read section .text failed");
        goto _error;
    }

    nblock = length / block_size;
    nsize = length / 4096 + (length % 4096 == 0 ? 0 : 1);
    printf("base = %x, length = %x\n", base, length);
    printf("nblock = %d, nsize = %d\n", nblock, nsize);
    printf("entry:%x\n",((length << 16) + nsize));

    ehdr.e_entry = (length << 16) + nsize;
    ehdr.e_shoff = base;

    for(i=0;i<length;i++){
        content[i] = ~content[i];
    }

    lseek(fd, 0, SEEK_SET);
    if(write(fd, &ehdr, sizeof(Elf32_Ehdr)) != sizeof(Elf32_Ehdr)){
        puts("Write ELFhead to .so failed");
        goto _error;
    }

    lseek(fd, base, SEEK_SET);
    if(write(fd, content, length) != length){
        puts("Write modified content to .so failed");
        goto _error;
    }

    puts("Completed");
    _error:
    free(content);
    free(shstr);
    close(fd);
    return 0;
}
