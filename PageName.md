# Introduction #

heisenberg是一款基于mysql协议的分库分表中间件，支持各种灵活的分库分表规则，
采用velocity的分库分表脚本进行自定义分库表.


# Details #

分库分表与应用脱离，分库表如同使用单库表一样 <br>
减少db 连接数压力  <br>
热重启配置  <br>
可水平扩容  <br>
遵守Mysql原生协议  <br>
无语言限制，mysqlclient,c,java等都可以使用  <br>
Heisenberg服务器通过管理命令可以查看，如连接数，线程池，结点等，并可以调整  <br>
采用velocity的分库分表脚本进行自定义分库表，相当的灵活 <br>