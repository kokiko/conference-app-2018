package io.github.droidkaigi.confsched2018.presentation

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.github.droidkaigi.confsched2018.R
import io.github.droidkaigi.confsched2018.model.Session
import io.github.droidkaigi.confsched2018.presentation.about.AboutThisAppActivity
import io.github.droidkaigi.confsched2018.presentation.about.AboutThisAppFragment
import io.github.droidkaigi.confsched2018.presentation.common.fragment.Findable
import io.github.droidkaigi.confsched2018.presentation.contributor.ContributorsActivity
import io.github.droidkaigi.confsched2018.presentation.contributor.ContributorsFragment
import io.github.droidkaigi.confsched2018.presentation.detail.SessionDetailActivity
import io.github.droidkaigi.confsched2018.presentation.detail.SessionDetailFragment
import io.github.droidkaigi.confsched2018.presentation.favorite.FavoriteSessionsFragment
import io.github.droidkaigi.confsched2018.presentation.feed.FeedFragment
import io.github.droidkaigi.confsched2018.presentation.map.MapActivity
import io.github.droidkaigi.confsched2018.presentation.map.MapFragment
import io.github.droidkaigi.confsched2018.presentation.search.SearchFragment
import io.github.droidkaigi.confsched2018.presentation.sessions.SessionsFragment
import io.github.droidkaigi.confsched2018.presentation.sessions.feedback.SessionsFeedbackActivity
import io.github.droidkaigi.confsched2018.presentation.sessions.feedback.SessionsFeedbackFragment
import io.github.droidkaigi.confsched2018.presentation.settings.SettingsActivity
import io.github.droidkaigi.confsched2018.presentation.settings.SettingsFragment
import io.github.droidkaigi.confsched2018.presentation.speaker.SpeakerDetailActivity
import io.github.droidkaigi.confsched2018.presentation.speaker.SpeakerDetailFragment
import io.github.droidkaigi.confsched2018.presentation.sponsors.SponsorsActivity
import io.github.droidkaigi.confsched2018.presentation.sponsors.SponsorsFragment
import io.github.droidkaigi.confsched2018.presentation.staff.StaffActivity
import io.github.droidkaigi.confsched2018.presentation.staff.StaffFragment
import io.github.droidkaigi.confsched2018.presentation.topic.TopicDetailActivity
import io.github.droidkaigi.confsched2018.presentation.topic.TopicDetailFragment
import io.github.droidkaigi.confsched2018.util.CustomTabsHelper
import javax.inject.Inject

class NavigationController @Inject constructor(private val activity: AppCompatActivity) {
    private val containerId: Int = R.id.content
    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    fun navigateToSessions() {
        replaceFragment(SessionsFragment.newInstance())
    }

    fun navigateToSearch() {
        replaceFragment(SearchFragment.newInstance())
    }

    fun navigateToFavoriteSessions() {
        replaceFragment(FavoriteSessionsFragment.newInstance())
    }

    fun navigateToFeed() {
        replaceFragment(FeedFragment.newInstance())
    }

    fun navigateToDetail(sessionId: String) {
        replaceFragment(SessionDetailFragment.newInstance(sessionId))
    }

    fun navigateToFeedback() {
        replaceFragment(SessionsFeedbackFragment.newInstance())
    }

    fun navigateToMap() {
        replaceFragment(MapFragment.newInstance())
    }

    fun navigateToSponsors() {
        replaceFragment(SponsorsFragment.newInstance())
    }

    fun navigateToSettings() {
        replaceFragment(SettingsFragment.newInstance())
    }

    fun navigateToAboutThisApp() {
        replaceFragment(AboutThisAppFragment.newInstance())
    }

    fun navigateToSpeakerDetail(speakerId: String, transitionName: String?) {
        replaceFragment(SpeakerDetailFragment.newInstance(speakerId, transitionName))
    }

    fun navigateToTopicDetail(topicId: Int) {
        replaceFragment(TopicDetailFragment.newInstance(topicId))
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager
                .beginTransaction()
                .replace(containerId, fragment, (fragment as? Findable)?.tagForFinding)
                .commitAllowingStateLoss()
    }

    fun navigateToContributor() {
        replaceFragment(ContributorsFragment.newInstance())
    }

    fun navigateToStaff() {
        replaceFragment(StaffFragment.newInstance())
    }

    fun navigateToMainActivity() {
        MainActivity.start(activity)
    }

    fun navigateToContributorActivity() {
        ContributorsActivity.start(activity)
    }

    fun navigateToStaffActivity() {
        StaffActivity.start(activity)
    }

    fun navigateToSessionDetailActivity(session: Session) {
        SessionDetailActivity.start(activity, session)
    }

    fun navigateToSessionsFeedbackActivity(session: Session.SpeechSession) {
        SessionsFeedbackActivity.start(activity, session)
    }

    fun navigateToMapActivity() {
        MapActivity.start(activity)
    }

    fun navigateToSponsorsActivity() {
        SponsorsActivity.start(activity)
    }

    fun navigateToSettingsActivity() {
        SettingsActivity.start(activity)
    }

    fun navigateToAboutThisAppActivity() {
        AboutThisAppActivity.start(activity)
    }

    fun navigateToSpeakerDetailActivity(speakerId: String) {
        SpeakerDetailActivity.start(activity, speakerId)
    }

    fun navigateToSpeakerDetailActivity(speakerId: String, sharedElement: Pair<View, String>) {
        SpeakerDetailActivity.start(activity, sharedElement, speakerId)
    }

    fun navigateToTopicDetailActivity(topicId: Int) {
        TopicDetailActivity.start(activity, topicId)
    }

    fun navigateToExternalBrowser(url: String) {
        val uri = run {
            val uri = Uri.parse(url)
            if (uri.host.contains("facebook")) {
                return@run Uri.parse(FACEBOOK_SCHEME + url)
            }
            uri
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val intentResolveInfo = activity.packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
        )
        val resolvePackageName = intentResolveInfo.activityInfo.packageName

        val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(ContextCompat.getColor(activity, R.color.primary))
                .build()
                .apply {
                    val appUri = Uri.parse("android-app://${activity.packageName}")
                    intent.putExtra(Intent.EXTRA_REFERRER, appUri)
                }

        val packageName = CustomTabsHelper.getPackageNameToUse(activity)
        if (resolvePackageName != null && resolvePackageName != packageName) {
            // Open specific app
            activity.startActivity(intent)
            return
        }
        packageName ?: run {
            // Cannot use custom tabs.
            activity.startActivity(customTabsIntent.intent.setData(uri))
            return
        }

        customTabsIntent.intent.`package` = packageName
        customTabsIntent.launchUrl(activity, uri)
    }

    fun navigateImplicitly(url: String?) {
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.resolveActivity(activity.packageManager)?.let {
                activity.startActivity(intent)
            }
        }
    }

    companion object {
        private const val FACEBOOK_SCHEME = "fb://facewebmodal/f?href="
    }
}
