import os
import platform

# 

if __name__ == '__main__':
    print('开始打tinker补丁包')
    sysstr = platform.system()
    cmdPrefix = './gradlew'
    if(sysstr =="Windows"):
         print ("Call Windows tasks")
         cmdPrefix = 'gradlew'
    elif(sysstr == "Linux"):
         print ("Call Linux tasks")
    else:
         print ("Other System tasks")

    suffix = '-PhostType=3'
    assemble = 'buildTinkerPatchOnlineRelease'
    cmdStr = 'cd android && %s %s %s' %(cmdPrefix, assemble, suffix)
    print(cmdStr)
    os.system(cmdStr)

