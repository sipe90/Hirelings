package net.sipe.hirelings.entity.player;

public class PlayerProperties {

    private boolean isDirty = false;

    // Non-persistent attributes
    private int linkEntityId = 0;


    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty() {
        isDirty = true;
    }

    public void setClean() {
        isDirty = false;
    }

    public void setLinkEntity(int entityId) {
        linkEntityId = entityId;
        setDirty();
    }

    public int getLinkEntityId() {
        return linkEntityId;
    }

    public boolean isLinking() {
        return linkEntityId != 0;
    }

    public void resetLinking() {
        linkEntityId = 0;
    }

}
