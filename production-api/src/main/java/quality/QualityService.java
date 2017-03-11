package quality;

import warehouse.StorageUnit;

public interface QualityService {

    void lock(StorageUnit unit);

    void unlock(StorageUnit unit, long recovered, long scrapped);
}
