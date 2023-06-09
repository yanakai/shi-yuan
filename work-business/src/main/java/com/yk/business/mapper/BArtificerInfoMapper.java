package com.yk.business.mapper;

import java.util.List;
import com.yk.business.domain.BArtificerInfo;

/**
 * 业务--技师Mapper接口
 *
 * @author yanakai@126.com
 * @date 2022-09-01
 */
public interface BArtificerInfoMapper
{
    /**
     * 查询业务--技师
     *
     * @param artificerId 业务--技师主键
     * @return 业务--技师
     */
    public BArtificerInfo selectBArtificerInfoByArtificerId(Long artificerId);

    /**
     * 查询业务--技师列表
     *
     * @param bArtificerInfo 业务--技师
     * @return 业务--技师集合
     */
    public List<BArtificerInfo> selectBArtificerInfoList(BArtificerInfo bArtificerInfo);

    /**
     * 新增业务--技师
     *
     * @param bArtificerInfo 业务--技师
     * @return 结果
     */
    public int insertBArtificerInfo(BArtificerInfo bArtificerInfo);

    /**
     * 修改业务--技师
     *
     * @param bArtificerInfo 业务--技师
     * @return 结果
     */
    public int updateBArtificerInfo(BArtificerInfo bArtificerInfo);

    /**
     * 删除业务--技师
     *
     * @param artificerId 业务--技师主键
     * @return 结果
     */
    public int deleteBArtificerInfoByArtificerId(Long artificerId);

    /**
     * 批量删除业务--技师
     *
     * @param artificerIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBArtificerInfoByArtificerIds(Long[] artificerIds);
    /**
     * @title getArtificerPaiZhongList
     * @description  首页--查询技师排钟列表
     * @param bArtificerInfo
     * @return java.util.List<com.yk.business.domain.BArtificerInfo>
     * @author yanakai@126.com
     * @date   2023/3/18
     */
    List<BArtificerInfo> getArtificerPaiZhongList(BArtificerInfo bArtificerInfo);
    /**
     * @title saveArtificerPaiZhongByList
     * @description  批量保存技师排钟信息
     * @param list
     * @return int
     * @author yanakai@126.com
     * @date   2023/3/18
     */
    int saveArtificerPaiZhongByList(List<BArtificerInfo> list);
}
