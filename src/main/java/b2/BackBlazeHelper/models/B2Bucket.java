package b2.BackBlazeHelper.models;

/**
 * Represents a Bucket present in the B2 API.
 */
public class B2Bucket {

    private String name, ID;
    private BucketType type;

    /**
     * Constructs a B2Bucket instance.
     *
     * @param name The name of the bucket, which is at least six characters and does not start with "b2-"
     * @param ID The ID of the bucket, which is randomly generated by Backblaze
     * @param type The privacy level of the bucket
     */
    public B2Bucket(String name, String ID, BucketType type){
        this.name = name;
        this.ID = ID;
        this.type = type;
    }

    /**
     * Gets the name of the bucket.
     *
     * @return The name of the bucket, which is at least six characters and does not start with "b2-"
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the ID of the bucket.
     *
     * @return The ID of the bucket, which is randomly generated by the Backblaze
     */
    public String getID(){
        return ID;
    }

    /**
     * Gets the privacy level of the bucket.
     *
     * @return The privacy level of the bucket
     */
    public BucketType getType(){
        return type;
    }

    /**
     * Sets the privacy level of the bucket.
     *
     * @param type The privacy level of the bucket
     */
    public void setType(BucketType type){
        this.type = type;
    }

}
