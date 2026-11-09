package com.catpuppyapp.puppygit.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catpuppyapp.puppygit.constants.SpecialCredential
import com.catpuppyapp.puppygit.data.entity.CredentialEntity
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt
import com.catpuppyapp.puppygit.utils.AppModel
import com.catpuppyapp.puppygit.utils.doActIfIndexGood

@Composable
fun CredentialSelector(
    credentialList:List<CredentialEntity>,
    selectedCredentialIdx:MutableIntState,
    title:String = stringResource(R.string.select_credential),
) {

    val activityContext = LocalContext.current

    val note = rememberSaveable { mutableStateOf("") }

    MySelectionContainer {
        DefaultPaddingText("$title: ")
    }

    Spacer(Modifier.height(5.dp))

    SingleSelectList(
        optionsList = credentialList,
        selectedOptionIndex = selectedCredentialIdx,
        menuItemFormatter = {_,value-> value?.name?:"" }
    )

    doActIfIndexGood(selectedCredentialIdx.intValue, credentialList) {
        if(SpecialCredential.NONE.equals_to(it)) {
            note.value = activityContext.getString(R.string.no_credential_will_be_used)
        }else if(SpecialCredential.MatchByDomain.equals_to(it)) {
            note.value = activityContext.getString(R.string.credential_match_by_domain_note)
        }else{
            note.value = ""
        }
    }

    if(note.value.isNotBlank()) {
        Spacer(Modifier.height(5.dp))
        MySelectionContainer {
            DefaultPaddingText(note.value, color = MyStyleKt.TextColor.getHighlighting())
        }
    }

    Spacer(Modifier.height(15.dp))

}
