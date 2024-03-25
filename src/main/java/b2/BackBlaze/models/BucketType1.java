package b2.BackBlaze.models;

public enum BucketType1 {
    ALL_PUBLIC("allPublic"),
    ALL_PRIVATE("allPrivate");

    private String identifier;

    /**
     * Constructs a new BucketType.
     *
     * @param identifier Internal identifier for the type used by the B2 API
     */
    BucketType1(String identifier){
        this.identifier = identifier;
    }

    /**
     * Returns the identifier of the type.
     *
     * @return Interal identifier for the type used by the B2 API
     */
    public String getIdentifier(){
        return identifier;
    }

    public static BucketType1 getByIdentifier(String name){
        for(BucketType1 type : BucketType1.values()){
            if(type.getIdentifier().equals(name)) return type;
        }
        return null;
    }

}
