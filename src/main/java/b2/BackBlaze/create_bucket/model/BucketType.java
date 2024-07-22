package b2.BackBlaze.create_bucket.model;

public enum BucketType {
    
    ALL_PUBLIC("allPublic"),
    ALL_PRIVATE("allPrivate");

    private String identifier;

    /**
     * Constructs a new BucketType.
     *
     * @param identifier Internal identifier for the type used by the B2 API
     */
    BucketType(String identifier){
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

    public static BucketType getByIdentifier(String name){
        for(BucketType type : BucketType.values()){
            if(type.getIdentifier().equals(name)) 
                return type;
        }
        return null;
    }

}
