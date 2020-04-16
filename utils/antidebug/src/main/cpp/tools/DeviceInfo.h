#include <sys/system_properties.h>

//读取序号
#define SERIALNO(value)  __system_property_get("ro.serialno",value)
//读取机型 
#define MODEl(value)  __system_property_get("ro.product.model",value)
//读取sdk版本
#define SDK_VERSION(value)  __system_property_get("ro.build.version.sdk",value)