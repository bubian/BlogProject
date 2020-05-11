#include "common.h"
#include <string.h>
#include <jni.h>
#include <unistd.h>
#include <string>
#include <vector>

JNIEnv *gJNIEnv;
jobject gMainActivityObject;
int gDownButtons;
int gLastDownButtons;
int gPressedButtons;
bool gSuspendState;
bool gQuitState;
std::string gUIString;
std::vector<char *> gPathList;

void Common_Init(void **extraDriverData)
{
	gDownButtons = 0;
	gLastDownButtons = 0;
	gPressedButtons = 0;
	gSuspendState = false;
	gQuitState = false;
}

void Common_Update()
{
	jstring text = gJNIEnv->NewStringUTF(gUIString.c_str());
	jclass mainActivityClass = gJNIEnv->GetObjectClass(gMainActivityObject);
    jmethodID updateScreenMethodID = gJNIEnv->GetMethodID(mainActivityClass, "updateScreen", "(Ljava/lang/String;)V");

    gJNIEnv->CallVoidMethod(gMainActivityObject, updateScreenMethodID, text);

    gJNIEnv->DeleteLocalRef(text);
    gJNIEnv->DeleteLocalRef(mainActivityClass);

    gUIString.clear();

    gPressedButtons = (gLastDownButtons ^ gDownButtons) & gDownButtons;
    gLastDownButtons = gDownButtons;

    if (gQuitState)
    {
    	gPressedButtons |= (1 << BTN_QUIT);
    }
}


extern "C"
{

} /* extern "C" */
