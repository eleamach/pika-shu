package com.example.pika_shu;

public class possede {
    private int id;
    private int current_pv;
    private int pokemonId;

    public possede(int pokemonId, int current_pv) {
        this.id = id;
        this.current_pv = current_pv;
        this.pokemonId = pokemonId;
    }

    public possede(){}
    public int getId(){return this.id;}
    public void setIDPokemon(int pokemonId){
        this.pokemonId = pokemonId;
    }
    public int getIdPokemon(){return this.pokemonId;}
    public void setID(int id){
        this.id = id;
    }
    public int getCurrentPv(){return this.current_pv;}
    public void setCurrentPv(int currentpv){
        this.current_pv = currentpv;
    }
}

