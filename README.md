# desktopcal_android

##同步原理：
1. desktopcal的数据都是储存在sqlite的数据库中，在windows下的目录为C:\Users\****\AppData\Roaming\DesktopCal\Db\calendar.db
  或者大家自行用everything或listart搜索一下，文件名为calendar.db
2. 我使用坚果云同步到手机，同步到手机后的目录为
/storage/emulated/0/Android/data/nutstore.android/cache/objectcache/1/calendar.db



##存在问题：
1. 如果windows下的desktopcal是打开的，那么calendar.db被使用，不能及时同步
2. 不同型号手机同步过来的calendar.db位置可能不同，可以在手机上用everything来搜索这个db文件的具体位置，然后修改代码，编译。
