@echo off

echo jar�t�@�C�����R�s�[���܂��B
copy target\MyMaid4.jar server\plugins\MyMaid4.jar
if not %errorlevel% == 0 (
    echo MyMaid4.jar NOT FOUND

    echo 5�b��ɃN���[�Y���܂��B
    timeout 5 /NOBREAK
    exit 1
)

echo Minecraft�T�[�o�ɑ΂��ă����[�h�R�}���h�����s���܂��B
java -jar server\mcrconapi-1.1.1.jar -a localhost -l rconpassword -n -c "rl confirm"

if not %errorlevel% == 0 (
    echo Minecraft�T�[�o���N�����Ă��Ȃ����߁A�N�����܂��B

    cd server
    java -jar paper-1.16.5.jar -nogui
    if %errorlevel% == 0 exit
)

echo 5�b��ɃN���[�Y���܂��B
timeout 5 /NOBREAK
exit
