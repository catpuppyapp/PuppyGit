package com.catpuppyapp.puppygit.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.catpuppyapp.puppygit.play.pro.R
import com.catpuppyapp.puppygit.style.MyStyleKt


@Composable
fun TokenInsteadOfPasswordHint() {
    MySelectionContainer {
        DefaultPaddingRow {
            Text(
                buildAnnotatedString {
                    append(stringResource(R.string.note_pat_instead_of_password))

                    //相关网页的可点击链接
                    append(" (")
                    withLink(LinkAnnotation.Url("https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens")) {
                        withStyle(style = SpanStyle(fontWeight = MyStyleKt.TextItem.defaultFontWeight(), color = MyStyleKt.ClickableText.getColor(), fontSize = MyStyleKt.ClickableText.fontSize)) {
                            append("GitHub")
                        }
                    }

                    append(" / ")

                    withLink(LinkAnnotation.Url("https://docs.gitlab.com/user/profile/personal_access_tokens/")) {
                        withStyle(style = SpanStyle(fontWeight = MyStyleKt.TextItem.defaultFontWeight(), color = MyStyleKt.ClickableText.getColor(), fontSize = MyStyleKt.ClickableText.fontSize)) {
                            append("GitLab")
                        }
                    }

                    append(" / ")

                    withLink(LinkAnnotation.Url("https://confluence.atlassian.com/bitbucketserver076/personal-access-tokens-1026534797.html")) {
                        withStyle(style = SpanStyle(fontWeight = MyStyleKt.TextItem.defaultFontWeight(), color = MyStyleKt.ClickableText.getColor(), fontSize = MyStyleKt.ClickableText.fontSize)) {
                            append("Bitbucket")
                        }
                    }

                    append(")")

                }
            )
        }
    }
}
