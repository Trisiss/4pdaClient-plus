package org.softeg.slartus.forpdaplus.classes.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.softeg.slartus.forpdaplus.App;
import org.softeg.slartus.forpdaplus.IntentActivity;
import org.softeg.slartus.forpdaplus.MainActivity;
import org.softeg.slartus.forpdaplus.R;
import org.softeg.slartus.forpdaplus.classes.MenuListDialog;
import org.softeg.slartus.forpdaplus.controls.imageview.ImgViewer;
import org.softeg.slartus.forpdaplus.download.DownloadsService;
import org.softeg.slartus.forpdaplus.notes.NoteDialog;
import org.softeg.slartus.forpdaplus.prefs.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.softeg.slartus.forpdaplus.video.PlayerActivity.PLAYER_UNSELECTED;

/**
 * Created by IntelliJ IDEA.
 * User: slartus
 * Date: 27.10.12
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public class ExtUrl {

    public static void showContextDialog(final Context context, final String title, final List<MenuListDialog> list) {
        final List<String> names = new ArrayList<>();
        for (MenuListDialog menuListDialog : list)
            names.add(menuListDialog.getTitle());

        new MaterialDialog.Builder(context)
                .title(title)
                .items(names.toArray(new CharSequence[names.size()]))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        list.get(which).getRunnable().run();
                    }
                })
                .show();
    }

    public static void openNewTab(Context context, Handler handler, String url) {
        if (!IntentActivity.tryShowUrl((Activity) context, handler, url, false, false))
            Toast.makeText(context, R.string.links_not_supported, Toast.LENGTH_SHORT).show();
    }

    public static void showInBrowser(Context context, String url) {
        if (!url.startsWith("http:") && !url.startsWith("https:")) {
            url = "https:" + url;
        }
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(Intent.createChooser(marketIntent, context.getString(R.string.choose)));
    }

    public static void shareIt(Context context, String subject, String text, String url) {
        Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendMailIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendMailIntent.setData(Uri.parse(url));
        sendMailIntent.setType("text/plain");

        context.startActivity(Intent.createChooser(sendMailIntent, context.getString(R.string.chare_via)));
    }

    public static void copyLinkToClipboard(Context context, String link) {
        StringUtils.copyToClipboard(context, link);
        Toast.makeText(context, R.string.link_copied_to_buffer, Toast.LENGTH_SHORT).show();
    }

    public static void addUrlSubMenu(final android.os.Handler handler, final Context context, List<MenuListDialog> list, final String url
            , final CharSequence id, final String title) {
        addUrlMenu(handler, context, list, url, id, title);
    }

    public static void addUrlMenu(final android.os.Handler handler, final Context context, List<MenuListDialog> list, final String url, final String title) {
        addTopicUrlMenu(handler, context, list, title, url, "", "", "", "", "", "");
    }

    public static void addTopicUrlMenu(final android.os.Handler handler, final Context context, List<MenuListDialog> list,
                                       final String title, final String url, final String body, final CharSequence topicId, final String topic,
                                       final String postId, final String userId, final String user) {

        list.add(new MenuListDialog(context.getString(R.string.open_in_browser), new Runnable() {
            @Override
            public void run() {
                showInBrowser(context, url);
            }
        }));

        list.add(new MenuListDialog(context.getString(R.string.share_link), new Runnable() {
            @Override
            public void run() {
                shareIt(context, title, url, url);
            }
        }));
        list.add(new MenuListDialog(context.getString(R.string.copy_link), new Runnable() {
            @Override
            public void run() {
                copyLinkToClipboard(context, url);
            }
        }));


        if (!TextUtils.isEmpty(topicId)) {
            list.add(new MenuListDialog(context.getString(R.string.create_note), new Runnable() {
                @Override
                public void run() {
                    NoteDialog.showDialog(handler, context,
                            title, body, url, topicId, topic,
                            postId, userId, user);
                }
            }));
        }
    }

    public static void showSelectActionDialog(final Context context,
                                              final String title,
                                              final String url) {

        CharSequence[] titles = {context.getString(R.string.open_in_browser), context.getString(R.string.share_link), context.getString(R.string.copy_link)};
        new MaterialDialog.Builder(context)
                .title(title)
                .items(titles)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                showInBrowser(context, url);
                                break;
                            case 1:
                                shareIt(context, title, url, url);
                                break;
                            case 2:
                                copyLinkToClipboard(context, url);
                                break;
                        }
                    }
                })
                .cancelable(true)
                .show();


    }

    public static void addUrlMenu(final android.os.Handler handler, final Context context, List<MenuListDialog> menu, final String url,
                                  final CharSequence id, final String title) {
        addTopicUrlMenu(handler, context, menu, title, url, url, id, title, "", "", "");
    }

    public static void showSelectActionDialog(final android.os.Handler handler, final Context context,
                                              final String title, final String body, final String url, final String topicId, final String topic,
                                              final String postId, final String userId, final String user) {
        ArrayList<String> titles =
                new ArrayList<>(Arrays.asList(
                        context.getString(R.string.open_in_new_tab),
                        context.getString(R.string.open_in),
                        context.getString(R.string.share_link),
                        context.getString(R.string.copy_link),
                        context.getString(R.string.create_note),
                        context.getString(R.string.save)));
        if (IntentActivity.isYoutube(url)) {
            int savedSelectedPlayer = Integer.parseInt(App.getInstance().getPreferences()
                    .getString(Preferences.KEY_YOUTUBE_PLAYER, Integer.toString(PLAYER_UNSELECTED)));
            if (savedSelectedPlayer != PLAYER_UNSELECTED) {
                titles.add(context.getString(R.string.reset_player));
            }
        }
        new MaterialDialog.Builder(context)
                .content(url)
                .items(titles)
                .itemsCallback((dialog, view, i, titles1) -> {
                    switch (i) {
                        case 0:
                            openNewTab(context, handler, url);
                            break;
                        case 1:
                            showInBrowser(context, url);
                            break;
                        case 2:
                            shareIt(context, title, url, url);
                            break;
                        case 3:
                            copyLinkToClipboard(context, url);
                            break;
                        case 4:
                            NoteDialog.showDialog(handler, context,
                                    title, body, url, topicId, topic,
                                    postId, userId, user);
                            break;
                        case 5:
                            DownloadsService.download(((MainActivity) context), url, false);
                            break;
                        case 6:
                            App.getInstance()
                                    .getPreferences()
                                    .edit()
                                    .putString(Preferences.KEY_YOUTUBE_PLAYER, Integer.toString(PLAYER_UNSELECTED))
                                    .apply();
                            break;
                        default:
                            break;
                    }
                })
                .cancelable(true)
                .show();
    }

    public static void showSelectActionDialog(final android.os.Handler handler, final Context context, final String url) {
        showSelectActionDialog(handler, context, "", "", url, "", "", "", "", "");
    }


    public static void showImageSelectActionDialog(final android.os.Handler handler, final Context context, final String url) {
        CharSequence[] titles = new CharSequence[]{context.getString(R.string.open), context.getString(R.string.open_in), context.getString(R.string.copy_link), context.getString(R.string.save)};
        new MaterialDialog.Builder(context)
                .content(url)
                .items(titles)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int i, CharSequence titles) {
                        switch (i) {
                            case 0:
//                                ImageViewActivity.startActivity(context, url);
                                ImgViewer.startActivity(context, url);
                                break;
                            case 1:
                                showInBrowser(context, url);
                                break;
                            case 2:
                                copyLinkToClipboard(context, url);
                                break;
                            case 3:
                                DownloadsService.download((Activity) context, url, false);
                                break;
                        }
                    }
                })
                .cancelable(true)
                .show();
    }
}
