import com.wmp.PublicTools.easter_egg_control.var.Var;

void main() {
    var read = new String(IO.readln("请输入一个标准的变量字符串：").getBytes(), StandardCharsets.UTF_8);
    Var obj = Var.StringToVar("random[你, 好, 世, 界]");
    IO.println(obj);
    while(true){
        IO.println(obj.toTargetStyle());
        if (!IO.readln("0-继续").equals("0")) {
            return;
        }
    }
}
