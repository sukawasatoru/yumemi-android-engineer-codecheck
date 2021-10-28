/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import com.google.android.material.composethemeadapter.MdcTheme

class TwoFragment : Fragment() {
    private val args: TwoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(inflater.context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val item = args.item
        (view as ComposeView).setContent {
            TwoFragmentCompose(
                ownerIconUrl = Uri.parse(item.ownerIconUrl),
                name = item.name,
                language = item.language,
                stars = item.stargazersCount,
                watchers = item.watchersCount,
                forks = item.forksCount,
                openIssues = item.openIssuesCount,
            )
        }
    }
}

@Composable
fun TwoFragmentCompose(
    ownerIconUrl: Uri,
    name: String,
    language: String,
    stars: Long,
    watchers: Long,
    forks: Long,
    openIssues: Long,
) {
    MdcTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .widthIn(max = 240.dp)
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally),
                painter = rememberImagePainter(ownerIconUrl),
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally),
                style = LocalTextStyle.current.copy(fontSize = 16.sp),
                text = name,
            )
            Box(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    style = LocalTextStyle.current.copy(fontSize = 14.sp),
                    text = language,
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterEnd),
                    style = LocalTextStyle.current.copy(fontSize = 12.sp),
                    text = "$stars stars",
                )
            }
            Column(
                modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(horizontal = 16.dp)
                .align(Alignment.End),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProvideTextStyle(value = TextStyle(fontSize = 12.sp)) {
                    Text(
                        modifier = Modifier.align(Alignment.End),
                        text = "$watchers watchers",
                    )
                    Text(
                        modifier = Modifier.align(Alignment.End),
                        text = "$forks forks",
                    )
                    Text(
                        modifier = Modifier.align(Alignment.End),
                        text = "$openIssues open issues",
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun PreviewTwoFragmentCompose() {
    TwoFragmentCompose(
        ownerIconUrl = Uri.parse("https://via.placeholder.com/350x150"),
        name = "JetBrains/kotlin",
        language = "Written in Kotlin",
        stars = 38530,
        watchers = 38530,
        forks = 4675,
        openIssues = 131,
    )
}
