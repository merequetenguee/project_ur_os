package ur_os.system;

public  class Register {
    byte data;
    RegisterType type;
    Register(byte data, RegisterType type) {
        this.data = data;
        this.type = type;

    }
    public byte getData() {
        return data;
    }
    public RegisterType getType() {return type;}
    public void setData(byte data){
        this.data = data;


}
 @Override
    public String toString() {
        return type + "[" + data + "]";
    }
}
