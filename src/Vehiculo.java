public class Vehiculo {
    private byte motor;
    private byte llantas;
    private byte peso;

    public Vehiculo(byte mo, byte llan, byte pes){
        setMotor(mo);
        setLlantas(llan);
        setPeso(pes);
    }

    public byte getMotor(){ return motor; }
    public byte getLlantas(){ return llantas; }
    public byte getPeso(){ return peso; }

    public void setMotor(byte mo) {
        if (mo < 0 || mo > 10) {
            throw new IllegalArgumentException("La caracteristica debe estar entre 0 y 10. Valor intentado: " + mo);
        }
        this.motor = mo;
    }

    public void setLlantas(byte llan) {
        if (llan < 0 || llan > 10) {
            throw new IllegalArgumentException("La caracteristica debe estar entre 0 y 10. Valor intentado: " + llan);
        }
        this.llantas = llan;
    }

    public void setPeso(byte pes) {
        if (pes < 0 || pes > 10) {
            throw new IllegalArgumentException("La caracteristica de peso debe estar entre 0 y 10. Valor intentado: " + pes);
        }
        this.peso = pes;
    }
}