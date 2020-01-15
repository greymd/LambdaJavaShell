# LambdaJavaShell

Call `/bin/sh` from Lambda function with Java runtime.
Be compatible with Java 8 or more.

```sh
$ gradle build
$ aws lambda update-function-code --function-name LambdaJavaShell --zip-file fileb://./build/libs/LambdaJavaShell.jar
$ aws lambda update-function-configuration --function-name LambdaJavaShell --handler 'LambdaJavaShell.App::handleRequest'
$ aws lambda invoke --function-name LambdaJavaShell --payload "{\"command\":\"$(echo ls | base64)\"}" /dev/stdout | awk 'NR==1{gsub("[\"{]","",$1);print $1}' | base64 --decode
com
LambdaJavaShell
META-INF
org
```

## Helpful bash function

```
lmjshell () {
  local _cmd
  _cmd="$(printf "%s" "$1" | base64)"
  aws lambda invoke --function-name LambdaJavaShell --payload '{"command":"'"$_cmd"'"}' /dev/stdout | awk 'NR==1{gsub("[\"{]","",$1);print $1}' | base64 --decode
}
```


```sh
$ lmjshell ls
com
LambdaJavaShell
META-INF
org
```

## Why ?

We can check detailed JVM behavior on the host.

```
$ lmjshell 'java -version 2>&1'
openjdk version "1.8.0_201"
OpenJDK Runtime Environment (build 1.8.0_201-b09)
OpenJDK 64-Bit Server VM (build 25.201-b09, mixed mode)

$ lmjshell 'ls -al /usr/bin/jps8 2>&1'
lrwxrwxrwx 1 root root 46 Dec 31 12:13 /usr/bin/jps8 -> /usr/lib/jvm/java-1.8.0-openjdk.x86_64/bin/jps

$ lmjshell 'ps alx 2>&1'
F   UID   PID  PPID PRI  NI    VSZ   RSS WCHAN  STAT TTY        TIME COMMAND
4   496     1     0  20   0 2394916 49748 futex_ Ssl ?          0:00 /usr/bin/java -XX:MaxHeapSize=445645k -XX:MaxMetaspaceSize=52429k -XX:ReservedCodeCacheSize=26214k -Xshare:on -XX:-TieredCompilation -XX:+UseSerialGC -Djava.net.preferIPv4Stack=true -jar /var/runtime/lib/LambdaJavaRTEntry-1.0.jar
0   496    44     1  20   0 115276  2964 do_wai S    ?          0:00 /bin/sh -c ps alx 2>&1
0   496    45    44  20   0 115148  2444 -      R    ?          0:00 ps alx
```
