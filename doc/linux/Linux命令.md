### cut（切割数据）
---
- cut -b 

  按照字节切分
  
  1. 取第一个字节
  
     echo "abcdef" | cut -b 1
  2. 取1到2字节位置切分
  
     echo "abcdef" | cut -b 1-2
      
     如果有汉字：echo "小黑屋" | cut -b 1-3

  3. 按照字节多个位置切分
    
     echo "abcdef" | cut -b 1,3,5
    
- cut -c
     
  按照字符区分
      
     echo "abcd技术小黑屋ef" | cut -c 7
     
- cut -d     
  
  按照分隔符切分（"-f 1" 是取分割后第几个）
  
  echo "A|BC|DEF|GHIJ" |  cut -d "|" -f 1
  
