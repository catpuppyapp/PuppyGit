package com.catpuppyapp.puppygit.dto

import com.catpuppyapp.puppygit.data.entity.RepoEntity

/**
 * 用在Repo List页面，因为会根据屏幕宽度决定每行数量，所以需要一个数据结构类记录每个仓库的在原始列表的真实索引，不然基于索引设置的状态之类的就无效了
 */
@Deprecated("发现用不到这个类，直接遍历的时候计算索引了，若用这个，反而麻烦")
data class RepoDtoWithIndex (
    val trueIndex:Int,
    val repoDto:RepoEntity
)
