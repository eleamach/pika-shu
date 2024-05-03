package com.example.pika_shu;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class Pokemon implements Parcelable{
    private int order;
    private String name;
    private int height;
    private int weight;
    private int frontResource;
    private POKEMON_TYPE type1;
    private POKEMON_TYPE type2;
    private int pv;
    private boolean found;

    public Pokemon() {}

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }


    public Pokemon(int order, String name, int frontResource,
                   POKEMON_TYPE type1, POKEMON_TYPE type2,
                   int height, int weight) {
        this.order = order;
        this.name = name;
        this.frontResource = frontResource;
        this.type1 = type1;
        this.type2 = type2;
        this.height = height;
        this.weight = weight;
        this.pv = getRandomNumberInRange(50,200);
        this.found = false;
    }

    public Pokemon(int order, String name, int frontResource,
                   POKEMON_TYPE type1, POKEMON_TYPE type2) {
        this.order = order;
        this.name = name;
        this.frontResource = frontResource;
        this.type1 = type1;
        this.type2 = type2;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public int getFrontResource() {
        return frontResource;
    }
    public void setFrontResource(int frontResource) {
        this.frontResource = frontResource;
    }
    public POKEMON_TYPE getType1() {
        return type1;
    }
    public void setType1(POKEMON_TYPE type1) {
        this.type1 = type1;
    }
    public POKEMON_TYPE getType2() {
        return type2;
    }
    public void setType2(POKEMON_TYPE type2) {
        this.type2 = type2;
    }
    public String getType1String() {
        return type1.name();
    }
    public String getType2String() {
        if (type2 != null) {
            return type2.name();
        } else {
            return null;
        }
    }

    public void setPV(int pv) {
        this.pv = pv;
    }
    public int getPV() {
        return pv;
    }
    public void setFound(boolean found) {
        this.found = found;
    }
    public boolean isFound() {
        return found;
    }
    protected Pokemon(Parcel in) {
        order = in.readInt();
        name = in.readString();
        height = in.readInt();
        weight = in.readInt();
        frontResource = in.readInt();
        type1 = POKEMON_TYPE.valueOf(in.readString());
        String type2String = in.readString();
        type2 = type2String != null ? POKEMON_TYPE.valueOf(type2String) : null;
        pv = in.readInt();
        found = in.readByte() != 0;
    }

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(order);
        parcel.writeString(name);
        parcel.writeInt(height);
        parcel.writeInt(weight);
        parcel.writeInt(frontResource);
        parcel.writeString(type1.name());
        parcel.writeString(type2 != null ? type2.name() : null);
        parcel.writeInt(pv);
        parcel.writeByte((byte) (found ? 1 : 0));
    }



    enum POKEMON_TYPE {
        Acier,
        Combat,
        Dragon,
        Eau,
        Electrique,
        Fee,
        Feu,
        Glace,
        Insecte,
        Normal,
        Plante,
        Poison,
        Psy,
        Roche,
        Sol,
        Spectre,
        Tenebre,
        Vol
    }}