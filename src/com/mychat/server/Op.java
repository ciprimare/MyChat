package com.mychat.server;


public enum Op {
    MESSAGE(0),
    LOGOUT(1);

    private int codOp;

    Op(final int codOp){
        this.codOp = codOp;
    }

    public Op forCodOp(final int codOp) {
        for (Op item : Op.values()){
            if(item.getCodOp() == codOp ){
                return item;
            }
        }

        return null;
    }
    public int getCodOp() {
        return codOp;
    }
}
