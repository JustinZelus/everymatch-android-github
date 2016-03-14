package com.everymatch.saas.util;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.Participation_Type;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataDate;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEvent_Activity;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.singeltones.Preferences;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sergata on 13/07/15.
 */
public class Utils {
    public static final String TAG = Utils.class.getSimpleName();

    private static final String SHORT_DATE_FORMAT = "%s, %s %s %s\u202F\u00B7  %s:%s";

    public static String getDay(int day) {
        String stringDay = null;
        switch (day) {
            case 1:
                stringDay = "Sunday";
                break;
            case 2:
                stringDay = "Monday";
                break;
            case 3:
                stringDay = "Tuesday";
                break;
            case 4:
                stringDay = "Wednesday";
                break;
            case 5:
                stringDay = "Thursday";
                break;
            case 6:
                stringDay = "Friday";
                break;
            case 7:
                stringDay = "Saturday";
                break;

        }
        return stringDay;
    }

    public static String getDateStringFromDataDate(DataDate dataDate, String format) {
        try {
            Date date = getDateDromDataDate(dataDate);
            SimpleDateFormat fmt = new SimpleDateFormat(format, getRealCulture());
            return fmt.format(date);

            //return dataDate.day + "." + dataDate.month + "." + dataDate.year;
        } catch (Exception ex) {
            return "";
        }
    }

    public static Locale getRealCulture() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return Locale.forLanguageTag(DataStore.getInstance().getCulture().replace("he-", "iw-"));
            return Locale.getDefault();
        } catch (Exception ex) {
            return Locale.getDefault();
        }
        //Locale.forLanguageTag(DataStore.getInstance().getCulture().replace("he-", "iw-")).toLanguageTag();
    }

    public static Date getDateDromDataDate(DataDate dataDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, dataDate.year);
        cal.set(Calendar.MONTH, dataDate.month - 1);
        cal.set(Calendar.DAY_OF_MONTH, dataDate.day);
        cal.set(Calendar.HOUR_OF_DAY, dataDate.hour);
        cal.set(Calendar.MINUTE, dataDate.minute);
        cal.set(Calendar.SECOND, dataDate.second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Calendar getCalendarFromDataDate(DataDate dataDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, dataDate.year);
        cal.set(Calendar.MONTH, dataDate.month);
        cal.set(Calendar.DAY_OF_MONTH, dataDate.day);
        cal.set(Calendar.HOUR_OF_DAY, dataDate.hour);
        cal.set(Calendar.MINUTE, dataDate.minute);
        cal.set(Calendar.SECOND, dataDate.second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Date getDateFromString(String stringDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(stringDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getStringDateFromDate(String stringDate) {
        Date date = getDateFromString(stringDate);
        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d, ''yy");
        return fmt.format(date);
    }

    public static String getDeviceLoacal() {
        String answer = Locale.US.getLanguage().replace("iw", "il") + "-" + Locale.US.getCountry();

        return answer;
    }


    public static String getPhoneCountryCode(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_COUNTRY = manager.getNetworkCountryIso();
        return DEVICE_COUNTRY;
    }

    public static String getAddress(Context context, double lat, double lon) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = null;
        try {
            List<Address> listAddresses = geocoder.getFromLocation(lat, lon, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                address = listAddresses.get(0).getAddressLine(0) + ", " + listAddresses.get(0).getAddressLine(1) + ", " + listAddresses.get(0).getAddressLine(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public static void SmoothScrollAnimation(final ScrollView scrollView, int position, int duration) {
        ValueAnimator realSmoothScrollAnimation = ValueAnimator.ofInt(scrollView.getScrollY(), position);
        realSmoothScrollAnimation.setDuration(duration);
        realSmoothScrollAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int scrollTo = (Integer) animation.getAnimatedValue();
                scrollView.scrollTo(0, scrollTo);
            }
        });

        realSmoothScrollAnimation.start();
    }

    public static int getScreenWidth(Context context) {
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        return screenWidth;
    }

    public static boolean isArrayEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isArrayListEmpty(ArrayList arrayList) {
        return arrayList == null || arrayList.size() == 0;
    }

    public static JSONObject convertKeyValueToJSON(LinkedTreeMap<String, Object> ltm) {
        JSONObject jo = new JSONObject();
        Object[] objs = ltm.entrySet().toArray();
        for (int l = 0; l < objs.length; l++) {
            Map.Entry o = (Map.Entry) objs[l];
            try {
                if (o.getValue() instanceof LinkedTreeMap)
                    jo.put(o.getKey().toString(), convertKeyValueToJSON((LinkedTreeMap<String, Object>) o.getValue()));
                else
                    jo.put(o.getKey().toString(), o.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jo;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getEventDate(DataDate date) {

        String finalValue = "";

        try {
            DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
            String month = dateFormatSymbols.getShortMonths()[date.month - 1];
            Calendar c = Calendar.getInstance();
            c.set(date.year, date.month - 1, date.day);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            String weekDay = dateFormatSymbols.getShortWeekdays()[dayOfWeek];
            String dayStr = String.format("%02d", date.day);
            String hours = String.format("%02d", date.hour);
            String minutes = String.format("%02d", date.minute);
            finalValue = String.format(Locale.getDefault(), SHORT_DATE_FORMAT, weekDay, month, dayStr, date.year, hours, minutes);
        } catch (Exception e) {
        }

        return finalValue;
    }

    public static String getEventSchedule(DataEvent mEvent) {
        String title = "";
        String details = "";
        if (mEvent.dataPublicEvent.schedule.from.isSameDay(mEvent.dataPublicEvent.schedule.to)) {
            title = getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.from, "EEE, MMM d, yyyy");
            if (mEvent.dataPublicEvent.schedule.to.hasEndTime()) {
                details = mEvent.dataPublicEvent.schedule.from.getHourString() + " - " + mEvent.dataPublicEvent.schedule.to.getHourString();
            } else {
                details = mEvent.dataPublicEvent.schedule.from.getHourString();
            }
        } else {
            title = getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.from, "MMM d") + " - " +
                    getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.to, "MMM d");

            String at = DataManager.getInstance().getResourceText(R.string.At) + " ";
            String from = getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.from, "MMM d ") + at + mEvent.dataPublicEvent.schedule.from.getHourString();
            String to = getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.to, "MMM d ") + at + mEvent.dataPublicEvent.schedule.to.getHourString();
            details = (from + " - " + to);
        }

        return details;
    }


    /**
     * Takes int color witch defined in xml and convert to its
     * corresponding name which comes from server settings
     */
    public static int ViewModeIntToIntColor(int integerColor) {
        Resources resources = EverymatchApplication.getContext().getResources();
        switch (integerColor) {
            case EMColor.PRIMARY:
                return resources.getColor(R.color.primary);
            case EMColor.SECONDARY:
                return resources.getColor(R.color.secondary);
            case EMColor.TERTIARY:
                return resources.getColor(R.color.tertiary);
            case EMColor.FONT:
                return resources.getColor(R.color.font);
            case EMColor.BACKGROUND:
                return resources.getColor(R.color.background);
            case EMColor.NIGHT:
                return resources.getColor(R.color.night);
            case EMColor.MOON:
                return resources.getColor(R.color.moon);
            case EMColor.MORNING:
                return resources.getColor(R.color.morning);
            case EMColor.FOG:
                return resources.getColor(R.color.fog);
            case EMColor.ERROR:
                return resources.getColor(R.color.error);
            case EMColor.POSITIVE:
                return resources.getColor(R.color.positive);
            case EMColor.NEGATIVE:
                return resources.getColor(R.color.negative);
            case EMColor.MAYBE:
                return resources.getColor(R.color.maybe);
            case EMColor.WHITE:
                return resources.getColor(R.color.white);
            case EMColor.INFO:
                return resources.getColor(R.color.info);
            case EMColor.SILVER:
                return resources.getColor(R.color.silver);
            default:
                return resources.getColor(R.color.primary);
        }
    }

    public static String ViewModeIntToStringColor(int integerColor) {
        switch (integerColor) {
            case EMColor.PRIMARY:
                return "primary_color";
            case EMColor.SECONDARY:
                return "secondary_color";
            case EMColor.TERTIARY:
                return "tertiary_color";
            case EMColor.FONT:
                return "font_color";
            case EMColor.BACKGROUND:
                return "background_color";
            case EMColor.NIGHT:
                return "night_color";
            case EMColor.MOON:
                return "moon_color";
            case EMColor.MORNING:
                return "morning_color";
            case EMColor.FOG:
                return "fog_color";
            case EMColor.ERROR:
                return "error_color";
            case EMColor.POSITIVE:
                return "positive_color";
            case EMColor.NEGATIVE:
                return "negative_color";
            case EMColor.MAYBE:
                return "maybe_color";
            case EMColor.WHITE:
                return "white_color";
            case EMColor.INFO:
                return "info_color";
            case EMColor.SILVER:
                return "silver_color";
            default:
                return "primary_color";
        }
    }

    public static String intToStringColor(int integerColor) {
        switch (integerColor) {
            case EMColor.PRIMARY:
                return "primary_color";
            case EMColor.SECONDARY:
                return "secondary_color";
            case EMColor.TERTIARY:
                return "tertiary_color";
            case EMColor.FONT:
                return "font_color";
            case EMColor.BACKGROUND:
                return "background_color";
            case EMColor.NIGHT:
                return "night_color";
            case EMColor.MOON:
                return "moon_color";
            case EMColor.MORNING:
                return "morning_color";
            case EMColor.FOG:
                return "fog_color";
            case EMColor.ERROR:
                return "error_color";
            case EMColor.POSITIVE:
                return "positive_color";
            case EMColor.NEGATIVE:
                return "negative_color";
            case EMColor.MAYBE:
                return "maybe_color";
            case EMColor.WHITE:
                return "white_color";
            case EMColor.INFO:
                return "info_color";
            case EMColor.SILVER:
                return "silver_color";
            default:
                return "primary_color";

        }
    }


    public static String getImageUrl(String imageUrl, int requiredWidth, int requiredHeight) {
        return getImageUrl(imageUrl, requiredWidth, requiredWidth, null);
    }

    public static String getImageUrl(String imageUrl, int requiredWidth, int requiredHeight, String mode) {
        if (TextUtils.isEmpty(imageUrl)) {
            return "";
        }

        imageUrl = imageUrl + "?";

        if (requiredWidth > 0) {

            imageUrl += "&width=" + requiredWidth;

            if (requiredHeight > 0) {
                imageUrl += "&height=" + requiredHeight;
            }
        }

        if (!TextUtils.isEmpty(mode)) {
            imageUrl += "&mode=" + mode;
        }

        return imageUrl;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String setFirstLetterUpperCase(String word) {
        if (word != null && word.length() > 1) {
            word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        }

        return word;
    }

    public static void doLogoutOperation() {

        // Clear preferences
        Preferences preferences = Preferences.getInstance();
        preferences.setAccessToken(null);
        preferences.setTokenType(null);
        preferences.setApplicationData(null);
        preferences.setExpireIn(0);
        preferences.setExpires(null);
        preferences.setTimestamp(null);

        // Clear singletones
        DataStore.getInstance().setApplicationData(null);
        DataStore.getInstance().setUserData(null);
    }

    public static String participantsTabPositionToType(int position) {
        switch (position) {
            case 0:
                return Participation_Type.PARTICIPATING;
            case 1:
                return Participation_Type.MAYBE;
            case 2:
                return Participation_Type.INVITED;
            case 3:
                return Participation_Type.PENDING;

            default:
                return Participation_Type.PARTICIPATING;
        }
    }

    public static String participantsTypeToTabTitle(String type) {
        switch (type) {
            case Participation_Type.PARTICIPATING:
                return Participation_Type.COMING;

            default:
                return type;
        }
    }

    public static Dialog createBlockingEmptyDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.DialogTheme);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        View view = new View(activity);
        dialog.setContentView(view);
        dialog.show();
        return dialog;
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String makeTextCamelCase(String text) {
        try {
            if (TextUtils.isEmpty(text)) {
                return "";
            }

            String[] words = text.split(" ");
            StringBuilder sb = new StringBuilder();
            if (words[0].length() > 0) {
                sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
                for (int i = 1; i < words.length; i++) {
                    sb.append(" ");
                    sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
                }
            }
            String titleCaseValue = sb.toString();
            return titleCaseValue;
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
        return "";
    }

    public static String getEventImportantAnswersText(DataEvent event) {

        String result = "";

        try {

            ResponseApplication responseApplication = DataStore.getInstance().getApplicationData();
            DataActivity dataActivity = responseApplication.getActivityById(event.activity_client_id);

            DataAnswer[] eventAnswers = event.profile.answers;

            for (DataEvent_Activity event_activity : dataActivity.getEvents()) {
                if (event.client_id.equals(event_activity.client_id)) {

                    for (DataQuestion question : event_activity.questions) {
                        if (question.is_important) {

                            for (DataAnswer dataAnswers : eventAnswers) {
                                if (dataAnswers.questions_id == question.questions_id) {

                                    if (TextUtils.isEmpty(dataAnswers.text_label)) {
                                        result += ", " + QuestionUtils.getAnsweredTitle(question, dataAnswers);

                                    } else {
                                        result += ", " + dataAnswers.text_label;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            result = result.substring(2, result.length());

        } catch (Exception e) {
        }

        return result;
    }

    public static boolean isUserMe(DataPeople dataPeople) {
        ResponseGetUser responseGetUser = DataStore.getInstance().getUser();
        return (responseGetUser != null && dataPeople != null && dataPeople.users_id.equals(responseGetUser.users_id));
    }

    public static boolean hasInternetConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) EverymatchApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isDataDateToday(DataDate dataDate) {
        Calendar c = Calendar.getInstance();
        return dataDate != null && c.get(Calendar.YEAR) == dataDate.year &&
                c.get(Calendar.MONTH) == dataDate.month && c.get(Calendar.DAY_OF_MONTH) == dataDate.day;
    }

    public static boolean isAfterDate(DataDate fromDate, DataDate toDate) {

        boolean result = false;

        if (fromDate.year > toDate.year) {
            result = true;
        } else if (fromDate.year == toDate.year) {
            if (fromDate.month > toDate.month) {
                result = true;
            } else if (fromDate.month == toDate.month) {
                if (fromDate.day > toDate.day) {
                    result = true;
                }
            }
        }

        return result;
    }

    public static boolean isAfterOrSameHour(DataDate fromDate, DataDate toDate) {

        boolean result = false;

        if (fromDate.year != toDate.year || fromDate.month != toDate.month || fromDate.day != toDate.day) {
            result = false;
        } else {
            if (fromDate.hour > toDate.hour) {
                result = true;
            } else if (fromDate.hour == toDate.hour) {
                if (fromDate.minute >= toDate.month) {
                    result = true;
                }
            }
        }

        return result;
    }

    public static boolean isEmpty(String str) {
        if (str == null)
            return true;
        if (str.trim().length() == 0)
            return true;
        return false;
    }

    public static String getHourMinSecFromSeconds(int seconds) {
        JSONObject answer = new JSONObject();
        String timeString = "";
        try {
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int sec = seconds % 60;

            if (hours > 0)
                timeString += String.format("%02d", hours);
            if (minutes >= 0) {
                if (hours > 0) timeString += ":";
                timeString += String.format("%02d", minutes);
            }
            if (sec >= 0) {
                if (minutes > 0) timeString += ":";
                timeString += String.format("%02d", sec);
            }

            answer.put("hours", "" + hours);
            //timeString = String.format("%02d:%02d:%02d", hours, minutes, sec);
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
        return timeString;
    }
}
