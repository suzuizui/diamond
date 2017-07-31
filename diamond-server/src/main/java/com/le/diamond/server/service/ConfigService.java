package com.le.diamond.server.service;

import com.le.diamond.common.Constants;
import com.le.diamond.md5.MD5;
import com.le.diamond.server.utils.GroupKey2;
import com.le.diamond.server.utils.SimpleReadWriteLock;
import com.le.diamond.server.utils.SingletonRepository.DataIdGroupIdCache;
import com.le.diamond.server.utils.event.EventDispatcher;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.le.diamond.server.utils.LogUtil.defaultLog;
import static com.le.diamond.server.utils.LogUtil.dumpLog;




public class ConfigService {
    
    static public int groupCount() {
        return cache.size();
    }

    static public boolean hasGroupKey(String groupKey) {
        return cache.containsKey(groupKey);
    }
    
    /**
     * ���������ļ���������md5.
     */
    static public boolean dump(String dataId, String group, String content, long lastModifiedTs) {
        final String groupKey = GroupKey2.getKey(dataId, group);
        
        makeSure(groupKey);
        final int lockResult = tryWriteLock(groupKey);
        assert (lockResult != 0);
        
        if (lockResult < 0) {
            dumpLog.warn("[dump-error] write lock failed. {}", groupKey);
            return false;
        }

        try {            
            DiskUtil.saveToDisk(dataId, group, content);
            final String md5 = MD5.getInstance().getMD5String(content);
            updateMd5(groupKey, md5, lastModifiedTs);
           /*
            dumpLog.info("[dump-ok] {}, length={}, md5={}, content={}", new Object[] { groupKey,
                    content.length(), md5, ContentUtils.truncateContent(content) });
            */
            return true;
        } catch (IOException ioe) {
            dumpLog.error("[dump-exception] save disk error. " + groupKey + ", " + ioe.toString(),
                    ioe);
            return false;
        } finally {
            releaseWriteLock(groupKey);
        }
    }
    
    /**
     * ɾ�������ļ���ɾ�����档
     */
    static public boolean remove(String dataId, String group) {
        final String groupKey = GroupKey2.getKey(dataId, group);
        final int lockResult = tryWriteLock(groupKey);

        if (0 == lockResult) { // ���ݲ�����
            dumpLog.info("[remove-ok] {} not exist.", groupKey);
            return true;
        }
        
        if (lockResult < 0) { // ����ʧ��
            dumpLog.warn("[remove-error] write lock failed. {}", groupKey);
            return false;
        }

        try {
            DiskUtil.removeConfigInfo(dataId, group);
            cache.remove(groupKey);
            EventDispatcher.fireEvent(new LocalDataChangeEvent(groupKey));

            //dumpLog.info("[remove-ok] delete local file. {}", groupKey);
            return true;
        } finally {
            releaseWriteLock(groupKey);
        }
    }
    
    static void updateMd5(String groupKey, String md5, long lastModifiedTs) {
        CacheItem cache = makeSure(groupKey);
        if (!cache.md5.equals(md5)) {
            cache.md5 = md5;
            cache.lastModifiedTs = lastModifiedTs;
            EventDispatcher.fireEvent(new LocalDataChangeEvent(groupKey));
        }
    }



    /**
     * ����cache��md5���㳤���ַ�����ʾû�и����ݡ�
     */
    static public String getContentMd5(String groupKey) {
        CacheItem item = cache.get(groupKey);
        return (null != item) ? item.md5 : Constants.NULL;
    }

    static public long getLastModifiedTs(String groupKey) {
        CacheItem item = cache.get(groupKey);
        return (null != item) ? item.lastModifiedTs : 0L;
    }

    static public boolean isUptodate(String groupKey, String md5) {
        String serverMd5 = ConfigService.getContentMd5(groupKey);
        return StringUtils.equals(md5, serverMd5);
    }

    /**
     * �����ݼӶ���������ɹ�������������{@link #releaseReadLock(String)}��ʧ������Ҫ��
     * 
     * @param groupKey
     * @return ���ʾû�����ݣ�ʧ�ܡ�������ʾ�ɹ���������ʾ��д�����¼���ʧ�ܡ�
     */
    static public int tryReadLock(String groupKey) {
        CacheItem groupItem = cache.get(groupKey);
        int result = (null == groupItem) ? 0 : (groupItem.rwLock.tryReadLock() ? 1 : -1);
        if (result < 0) {
            defaultLog.warn("[read-lock] failed, {}, {}", result, groupKey);
        }
        return result;
    }
    
    static public void releaseReadLock(String groupKey) {
        CacheItem item = cache.get(groupKey);
        if (null != item) {
            item.rwLock.releaseReadLock();
        }
    }
    
    /**
     * �����ݼ�д��������ɹ�������������{@link #releaseWriteLock(String)}��ʧ������Ҫ��
     * 
     * @param groupKey
     * @return ���ʾû�����ݣ�ʧ�ܡ�������ʾ�ɹ���������ʾ����ʧ�ܡ�
     */
    static int tryWriteLock(String groupKey) {
        CacheItem groupItem = cache.get(groupKey);
        int result = (null == groupItem) ? 0 : (groupItem.rwLock.tryWriteLock() ? 1 : -1);
        if (result < 0) {
            defaultLog.warn("[write-lock] failed, {}, {}", result, groupKey);
        }
        return result;
    }
    
    static void releaseWriteLock(String groupKey) {
        CacheItem groupItem = cache.get(groupKey);
        if (null != groupItem) {
            groupItem.rwLock.releaseWriteLock();
        }
    }
    
    
    static CacheItem makeSure(final String groupKey) {
        CacheItem item = cache.get(groupKey);
        if (null != item) {
            return item;
        }
        CacheItem tmp = new CacheItem(groupKey);
        item = cache.putIfAbsent(groupKey, tmp);
        return (null == item) ? tmp : item;
    }    
    
    // =========================

    static final Logger log = LoggerFactory.getLogger(ConfigService.class);

    static private final ConcurrentHashMap<String/*groupKey*/, CacheItem> cache =
            new ConcurrentHashMap<String, CacheItem>();
}


class CacheItem {
    CacheItem(String groupKey) {
        this.groupKey = DataIdGroupIdCache.getSingleton(groupKey);
    }
    
    final String groupKey;
    volatile String md5 = Constants.NULL;
    volatile long lastModifiedTs;
    SimpleReadWriteLock rwLock = new SimpleReadWriteLock();
}
