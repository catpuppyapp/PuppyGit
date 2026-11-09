package com.catpuppyapp.puppygit.screen.content.listitem

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.catpuppyapp.puppygit.compose.TwoLineTextsAndIcons
import com.catpuppyapp.puppygit.data.entity.RepoEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.settings.AppSettings
import com.catpuppyapp.puppygit.settings.util.AutomationUtil
import com.catpuppyapp.puppygit.utils.appendSecondsUnit


@Composable
fun RepoNameAndIdItem(
    settings: AppSettings,
    selected: Boolean,
    appPackageName:String,
    repoEntity: RepoEntity,
    trailIconWidth: Dp,
    trailIcons: @Composable BoxScope.(containerModifier:Modifier)->Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,
    ) {
        TwoLineTextsAndIcons(
            text1 = repoEntity.repoName,
            text2 = if(selected) {
                AutomationUtil.getAppAndRepoSpecifiedSettingsActuallyBeUsed(appPackageName, repoEntity.id, settings).let {
                    stringResource(R.string.pull_interval)+": "+appendSecondsUnit(it.getPullIntervalFormatted())+", "+ stringResource(R.string.push_delay)+": "+appendSecondsUnit(it.getPushDelayFormatted())
                }
            } else {
//               // repoEntity.id
                ""
            },
            trailIconWidth = trailIconWidth,
            trailIcons = trailIcons
        )
    }

}

