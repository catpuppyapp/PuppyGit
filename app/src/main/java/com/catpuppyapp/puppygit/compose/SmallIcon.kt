package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.play.pro.R
import com.github.git24j.core.Repository

@Composable
fun SmallIcon(
    modifier: Modifier = Modifier.size(12.dp).padding(end = 2.dp),
    imageVector: ImageVector,
    contentDescription: String?
) {
    Icon(
        modifier = modifier,
        imageVector = imageVector,
        contentDescription = contentDescription,
    )
}


@Composable
fun ReadOnlyIcon() {
    SmallIcon(
        imageVector = Icons.Outlined.Lock,
        contentDescription = stringResource(R.string.read_only),
    )
}


@Composable
fun IconOfRepoState(repoState:Int) {
    if(repoState == Repository.StateT.MERGE.bit) {
        SmallIcon(
            imageVector = Icons.Filled.Merge,
            contentDescription = stringResource(R.string.merge)
        )
    }else if(repoState == Repository.StateT.REBASE_MERGE.bit) {
        SmallIcon(
            imageVector = ImageVector.vectorResource(R.drawable.git_rebase),
            contentDescription = stringResource(R.string.rebase)
        )
    }else if(repoState == Repository.StateT.CHERRYPICK.bit) {
        SmallIcon(
            imageVector = ImageVector.vectorResource(R.drawable.outline_nutrition_24),
            contentDescription = stringResource(R.string.cherrypick)
        )
    }
}
