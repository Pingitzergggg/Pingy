public enum test {
    HALO,
    HALLLO
}

void main() throws ParseException {
    Divider source = new Divider("./samples/test.pin");
    System.out.println(source.getCodeBase());
    Register engine = new Register(source.getCodeBase());
    engine.start();
    Pool.getInstance().showOutputStream();
    Accessor.getInstance().printVariableTable();

}