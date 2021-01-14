package com.example.secoco.entities;

public class UsuarioAPI {

    private String U, N, I, M, Z;

    //Posiblemente se elimine
    public UsuarioAPI(String u, String n, String i, String m, String z) {
        U = u;
        N = n;
        I = i;
        M = m;
        Z = z;
    }

    public String getU() {
        return U;
    }

    public String getN() {
        return N;
    }

    public String getI() {
        return I;
    }

    public String getM() {
        return M;
    }

    public String getZ() {
        return Z;
    }

    public void setU(String u) {
        U = u;
    }

    public void setN(String n) {
        N = n;
    }

    public void setI(String i) {
        I = i;
    }

    public void setM(String m) {
        M = m;
    }

    public void setZ(String z) {
        Z = z;
    }
}
