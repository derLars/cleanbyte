package com.derlars.smsplusplus.Utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.derlars.smsplusplus.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneFormatizer {
    public final static String TAG = "LARS";

    private static List<Region> regions;

    private static String selectedRegion;

    private final static PhoneFormatizer INSTANCE = new PhoneFormatizer();

    private static List<String> usedRegions = new ArrayList<>();

    private PhoneFormatizer() {

    }

    public PhoneFormatizer getInstance() {
        return INSTANCE;
    }

    public static String formatizeToValidPhoneNumber(String phone, String region, Context context) {
        usedRegions.add(region);

        selectedRegion = region;

        return formatizeToValidPhoneNumber(phone, context);
    }

    public static String formatizeToValidPhoneNumber(String phone, Context context) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber formatter = null;

        try {
            if (selectedRegion != null) {
                formatter = phoneUtil.parse(phone, selectedRegion);
            }
            if (formatter == null || !phoneUtil.isValidNumber(formatter)) {
                formatter = phoneUtil.parse(phone, getNetworkRegion(context));
            }
            if (formatter != null && phoneUtil.isValidNumber(formatter)) {
                String number = phoneUtil.format(formatter, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                String newRegion = phoneUtil.getRegionCodeForNumber(formatter);
                usedRegions.add(newRegion);

                if(selectedRegion == null) {
                    String[] splitted = number.split(" ");
                    if(splitted.length > 0) {
                        selectedRegion = phoneUtil.getRegionCodeForNumber(formatter);
                        Log.d(TAG,"Setting selected region to: " + selectedRegion);
                    }
                }
                return number;
            }
            if(!phoneUtil.isValidNumber(formatter)) {
                for(String region : usedRegions) {
                    formatter = phoneUtil.parse(phone, region);
                    if(phoneUtil.isValidNumber(formatter)) {
                        String number = phoneUtil.format(formatter, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                        return number;
                    }
                }
            }
        }catch(NumberParseException npe) {

        }

        return phone;
    }

    public static boolean isValidPhoneNumber(String phone, Context context) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber formatter = null;

        try {
            if (selectedRegion != null) {
                formatter = phoneUtil.parse(phone, selectedRegion);
            }
            if (formatter == null || !phoneUtil.isValidNumber(formatter)) {
                formatter = phoneUtil.parse(phone, getNetworkRegion(context));
            }
            if (formatter != null && phoneUtil.isValidNumber(formatter)) {
                return true;
            }
        }catch(NumberParseException npe) {

        }

        return false;
    }

    public static String getNetworkRegion(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso().toUpperCase();

        if(countryCode.length() <= 0) {
            countryCode = tm.getSimCountryIso().toUpperCase();
        }

        return countryCode;
    }

    public static String getNetworkCountryCode(Context context) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        String region = getNetworkRegion(context);

        if(region.length() <= 0) {
            return region;
        }

        return "+" + phoneUtil.getCountryCodeForRegion(region);
    }

    public static String[] getAllcountryFlags() {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        List<String> countryFlags = new ArrayList<>();

        for(String region : phoneUtil.getSupportedRegions()) {
            if(!countryFlags.contains("+" + phoneUtil.getCountryCodeForRegion(region))) {
                countryFlags.add("+" + phoneUtil.getCountryCodeForRegion(region));
            }
        }

        Collections.sort(countryFlags);

        String[] returnValues = new String[countryFlags.size()];
        returnValues = countryFlags.toArray(returnValues);

        return returnValues;
    }

    public static List<Region> getRegions() {
        if(regions == null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

            regions = new ArrayList<>();
            //dials = new ArrayList<>();
            //flags = new ArrayList<>();

            Map<String, Integer> countryFlags = new HashMap<>();
            {
                countryFlags.put("ST",R.drawable.flag_st);
                countryFlags.put("NG",R.drawable.flag_ng);
                countryFlags.put("AG",R.drawable.flag_ag);
                countryFlags.put("CI",R.drawable.flag_ci);
                countryFlags.put("ME",R.drawable.flag_me);
                countryFlags.put("IL",R.drawable.flag_il);
                countryFlags.put("SE",R.drawable.flag_se);
                countryFlags.put("KW",R.drawable.flag_kw);
                countryFlags.put("PS",R.drawable.flag_ps);
                countryFlags.put("MY",R.drawable.flag_my);
                countryFlags.put("BS",R.drawable.flag_bs);
                countryFlags.put("CF",R.drawable.flag_cf);
                countryFlags.put("DJ",R.drawable.flag_dj);
                countryFlags.put("KH",R.drawable.flag_kh);
                countryFlags.put("LB",R.drawable.flag_lb);
                countryFlags.put("MR",R.drawable.flag_mr);
                countryFlags.put("WS",R.drawable.flag_ws);
                countryFlags.put("AR",R.drawable.flag_ar);
                countryFlags.put("SO",R.drawable.flag_so);
                countryFlags.put("MA",R.drawable.flag_ma);
                countryFlags.put("KR",R.drawable.flag_kr);
                countryFlags.put("CM",R.drawable.flag_cm);
                countryFlags.put("TZ",R.drawable.flag_tz);
                countryFlags.put("JP",R.drawable.flag_jp);
                countryFlags.put("MZ",R.drawable.flag_mz);
                countryFlags.put("OM",R.drawable.flag_om);
                countryFlags.put("CR",R.drawable.flag_cr);
                countryFlags.put("GR",R.drawable.flag_gr);
                countryFlags.put("GD",R.drawable.flag_gd);
                countryFlags.put("LU",R.drawable.flag_lu);
                countryFlags.put("LS",R.drawable.flag_ls);
                countryFlags.put("ZW",R.drawable.flag_zw);
                countryFlags.put("TO",R.drawable.flag_to);
                countryFlags.put("LR",R.drawable.flag_lr);
                countryFlags.put("RU",R.drawable.flag_ru);
                countryFlags.put("US",R.drawable.flag_us);
                countryFlags.put("HR",R.drawable.flag_hr);
                countryFlags.put("SV",R.drawable.flag_sv);
                countryFlags.put("MC",R.drawable.flag_mc);
                countryFlags.put("LA",R.drawable.flag_la);
                countryFlags.put("CO",R.drawable.flag_co);
                countryFlags.put("ML",R.drawable.flag_ml);
                countryFlags.put("CU",R.drawable.flag_cu);
                countryFlags.put("MG",R.drawable.flag_mg);
                countryFlags.put("GA",R.drawable.flag_ga);
                countryFlags.put("TM",R.drawable.flag_tm);
                countryFlags.put("NI",R.drawable.flag_ni);
                countryFlags.put("BT",R.drawable.flag_bt);
                countryFlags.put("TL",R.drawable.flag_tl);
                countryFlags.put("AT",R.drawable.flag_at);
                countryFlags.put("UZ",R.drawable.flag_uz);
                countryFlags.put("CZ",R.drawable.flag_cz);
                countryFlags.put("BF",R.drawable.flag_bf);
                countryFlags.put("MM",R.drawable.flag_mm);
                countryFlags.put("UY",R.drawable.flag_uy);
                countryFlags.put("ET",R.drawable.flag_et);
                countryFlags.put("MU",R.drawable.flag_mu);
                countryFlags.put("CV",R.drawable.flag_cv);
                countryFlags.put("BH",R.drawable.flag_bh);
                countryFlags.put("GN",R.drawable.flag_gn);
                countryFlags.put("IS",R.drawable.flag_is);
                countryFlags.put("EE",R.drawable.flag_ee);
                countryFlags.put("FJ",R.drawable.flag_fj);
                countryFlags.put("FM",R.drawable.flag_fm);
                countryFlags.put("SA",R.drawable.flag_sa);
                countryFlags.put("CH",R.drawable.flag_ch);
                countryFlags.put("MT",R.drawable.flag_mt);
                countryFlags.put("CL",R.drawable.flag_cl);
                countryFlags.put("GT",R.drawable.flag_gt);
                countryFlags.put("GW",R.drawable.flag_gw);
                countryFlags.put("DM",R.drawable.flag_dm);
                countryFlags.put("PL",R.drawable.flag_pl);
                countryFlags.put("VC",R.drawable.flag_vc);
                countryFlags.put("PH",R.drawable.flag_ph);
                countryFlags.put("LI",R.drawable.flag_li);
                countryFlags.put("LY",R.drawable.flag_ly);
                countryFlags.put("BI",R.drawable.flag_bi);
                countryFlags.put("NZ",R.drawable.flag_nz);
                countryFlags.put("SD",R.drawable.flag_sd);
                countryFlags.put("MX",R.drawable.flag_mx);
                countryFlags.put("BW",R.drawable.flag_bw);
                countryFlags.put("BN",R.drawable.flag_bn);
                countryFlags.put("NR",R.drawable.flag_nr);
                countryFlags.put("EC",R.drawable.flag_ec);
                countryFlags.put("KE",R.drawable.flag_ke);
                countryFlags.put("AE",R.drawable.flag_ae);
                countryFlags.put("ER",R.drawable.flag_er);
                countryFlags.put("GB",R.drawable.flag_gb);
                countryFlags.put("BR",R.drawable.flag_br);
                countryFlags.put("SI",R.drawable.flag_si);
                countryFlags.put("EG",R.drawable.flag_eg);
                countryFlags.put("SL",R.drawable.flag_sl);
                countryFlags.put("PY",R.drawable.flag_py);
                countryFlags.put("SS",R.drawable.flag_ss);
                countryFlags.put("VA",R.drawable.flag_va);
                countryFlags.put("SY",R.drawable.flag_sy);
                countryFlags.put("FR",R.drawable.flag_fr);
                countryFlags.put("BZ",R.drawable.flag_bz);
                countryFlags.put("SG",R.drawable.flag_sg);
                countryFlags.put("BY",R.drawable.flag_by);
                countryFlags.put("DO",R.drawable.flag_do);
                countryFlags.put("IT",R.drawable.flag_it);
                countryFlags.put("KI",R.drawable.flag_ki);
                countryFlags.put("AU",R.drawable.flag_au);
                countryFlags.put("PK",R.drawable.flag_pk);
                countryFlags.put("KP",R.drawable.flag_kp);
                countryFlags.put("HU",R.drawable.flag_hu);
                countryFlags.put("LC",R.drawable.flag_lc);
                countryFlags.put("CA",R.drawable.flag_ca);
                countryFlags.put("ZA",R.drawable.flag_za);
                countryFlags.put("CN",R.drawable.flag_cn);
                countryFlags.put("GH",R.drawable.flag_gh);
                countryFlags.put("GY",R.drawable.flag_gy);
                countryFlags.put("BD",R.drawable.flag_bd);
                countryFlags.put("IR",R.drawable.flag_ir);
                countryFlags.put("QA",R.drawable.flag_qa);
                countryFlags.put("TH",R.drawable.flag_th);
                countryFlags.put("HN",R.drawable.flag_hn);
                countryFlags.put("KN",R.drawable.flag_kn);
                countryFlags.put("MH",R.drawable.flag_mh);
                countryFlags.put("CY",R.drawable.flag_cy);
                countryFlags.put("BA",R.drawable.flag_ba);
                countryFlags.put("TV",R.drawable.flag_tv);
                countryFlags.put("PE",R.drawable.flag_pe);
                countryFlags.put("SR",R.drawable.flag_sr);
                countryFlags.put("VE",R.drawable.flag_ve);
                countryFlags.put("AD",R.drawable.flag_ad);
                countryFlags.put("RS",R.drawable.flag_rs);
                countryFlags.put("PG",R.drawable.flag_pg);
                countryFlags.put("JM",R.drawable.flag_jm);
                countryFlags.put("BE",R.drawable.flag_be);
                countryFlags.put("CD",R.drawable.flag_cd);
                countryFlags.put("TT",R.drawable.flag_tt);
                countryFlags.put("XK",R.drawable.flag_xk);
                countryFlags.put("BB",R.drawable.flag_bb);
                countryFlags.put("FI",R.drawable.flag_fi);
                countryFlags.put("TN",R.drawable.flag_tn);
                countryFlags.put("NL",R.drawable.flag_nl);
                countryFlags.put("TJ",R.drawable.flag_tj);
                countryFlags.put("JO",R.drawable.flag_jo);
                countryFlags.put("NP",R.drawable.flag_np);
                countryFlags.put("SM",R.drawable.flag_sm);
                countryFlags.put("TD",R.drawable.flag_td);
                countryFlags.put("RO",R.drawable.flag_ro);
                countryFlags.put("DK",R.drawable.flag_dk);
                countryFlags.put("HT",R.drawable.flag_ht);
                countryFlags.put("KG",R.drawable.flag_kg);
                countryFlags.put("IQ",R.drawable.flag_iq);
                countryFlags.put("VU",R.drawable.flag_vu);
                countryFlags.put("NE",R.drawable.flag_ne);
                countryFlags.put("PT",R.drawable.flag_pt);
                countryFlags.put("PW",R.drawable.flag_pw);
                countryFlags.put("ZM",R.drawable.flag_zm);
                countryFlags.put("MN",R.drawable.flag_mn);
                countryFlags.put("RW",R.drawable.flag_rw);
                countryFlags.put("IE",R.drawable.flag_ie);
                countryFlags.put("AO",R.drawable.flag_ao);
                countryFlags.put("TG",R.drawable.flag_tg);
                countryFlags.put("AL",R.drawable.flag_al);
                countryFlags.put("AM",R.drawable.flag_am);
                countryFlags.put("SB",R.drawable.flag_sb);
                countryFlags.put("LT",R.drawable.flag_lt);
                countryFlags.put("DE",R.drawable.flag_de);
                countryFlags.put("MV",R.drawable.flag_mv);
                countryFlags.put("SK",R.drawable.flag_sk);
                countryFlags.put("AF",R.drawable.flag_af);
                countryFlags.put("LK",R.drawable.flag_lk);
                countryFlags.put("GQ",R.drawable.flag_gq);
                countryFlags.put("MK",R.drawable.flag_mk);
                countryFlags.put("SN",R.drawable.flag_sn);
                countryFlags.put("VN",R.drawable.flag_vn);
                countryFlags.put("SC",R.drawable.flag_sc);
                countryFlags.put("ES",R.drawable.flag_es);
                countryFlags.put("CG",R.drawable.flag_cg);
                countryFlags.put("NO",R.drawable.flag_no);
                countryFlags.put("GM",R.drawable.flag_gm);
                countryFlags.put("TW",R.drawable.flag_tw);
                countryFlags.put("UA",R.drawable.flag_ua);
                countryFlags.put("MD",R.drawable.flag_md);
                countryFlags.put("KZ",R.drawable.flag_kz);
                countryFlags.put("LV",R.drawable.flag_lv);
                countryFlags.put("NA",R.drawable.flag_na);
                countryFlags.put("KM",R.drawable.flag_km);
                countryFlags.put("UG",R.drawable.flag_ug);
                countryFlags.put("BJ",R.drawable.flag_bj);
                countryFlags.put("IN",R.drawable.flag_in);
                countryFlags.put("BO",R.drawable.flag_bo);
                countryFlags.put("BG",R.drawable.flag_bg);
                countryFlags.put("DZ",R.drawable.flag_dz);
                countryFlags.put("YE",R.drawable.flag_ye);
                countryFlags.put("GE",R.drawable.flag_ge);
                countryFlags.put("ID",R.drawable.flag_id);
                countryFlags.put("TR",R.drawable.flag_tr);
                countryFlags.put("SZ",R.drawable.flag_sz);
                countryFlags.put("MW",R.drawable.flag_mw);
                countryFlags.put("PA",R.drawable.flag_pa);
                countryFlags.put("AZ",R.drawable.flag_az);
            }//Adding the flags

            for(String region : phoneUtil.getSupportedRegions()) {
                if(countryFlags.containsKey(region)) {
                    String d = "+" + phoneUtil.getCountryCodeForRegion(region);

                    regions.add(new Region(region,d,countryFlags.get(region)));
                }
            }

            Collections.sort(regions);
        }

        return regions ;
    }
}
