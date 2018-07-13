package com.calculate.contest.contest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.RangeValueIterator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sPref;
    final String contest_score = "181.458";
    EditText ed_score;
    TextView prior1_view, prior2_view, prior3_view, prior_err_view, all_zayav_view, before_me_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed_score = findViewById(R.id.editText_score);
        prior1_view = findViewById(R.id.textView_prioritet1);
        prior2_view = findViewById(R.id.textView_prioritet2);
        prior3_view = findViewById(R.id.textView_prioritet3);
        prior_err_view = findViewById(R.id.textView_prior_err);
        all_zayav_view = findViewById(R.id.textView_all_zayav);
        before_me_view = findViewById(R.id.textView_before);
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    public void onClick_parse(View v) {
        final String[] urls_osvita = {"https://vstup.osvita.ua/r21/92/460953/","https://vstup.osvita.ua/r21/92/461081/"},
             urls_abit = {"https://abit-poisk.org.ua/rate2017/direction/46784/?page=","https://abit-poisk.org.ua/rate2017/direction/46789"};
        RadioButton radio_pi = findViewById(R.id.radioButton_pi);
        int i=radio_pi.isChecked()?0:1;
        Parsing(urls_osvita[i]);
        CheckBox checkbox = findViewById(R.id.checkBox);
        if(checkbox.isChecked())preview_year(urls_abit[i]);
    }

    void saveData() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(contest_score, ed_score.getText().toString());
        ed.commit();
    }

    void loadData() {
        sPref = getPreferences(MODE_PRIVATE);
        ed_score.setText(sPref.getString(contest_score, "181.458"));
    }

    private void Parsing(final String url) {
        final TextView view_err = findViewById(R.id.textView_errors_count);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                Elements abits = null;
                String last_upd = null;
                String budgets = null;
                int errors = 0;
                do {
                    try {
                        doc = Jsoup.connect(url).get();
                        abits = doc.select("table.rwd-table > tbody > tr[class*=rstatus]:not(.hdn)");
                        if(last_upd==null)last_upd=doc.selectFirst("div.last-update").text();
                        if(budgets==null) budgets = doc.select("div[class=table-of-specs-item panel-mobile] > b").next().next().next().first().text();

                    } catch (IOException e) {
                        errors++;
                        final int temp_err = errors;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view_err.setTextColor(Color.RED);
                                view_err.setText(temp_err);
                            }
                        });
                    }
                } while (doc == null);
                double my_score = Double.parseDouble(ed_score.getText().toString());
                int prior1 = 0, prior2 = 0, prior3 = 0, p_errors = 0, counts_before_me = 0;
                for (Element abit : abits) {
                    try {
                        int prior = Integer.parseInt(abit.select("td[data-th=П]").first().text());
                        double score = Double.parseDouble(abit.select("td[data-th=Бал]").first().text());
                        if (my_score >= score) break;
                        switch (prior) {
                            case 1:
                                prior1++;
                                break;
                            case 2:
                                prior2++;
                                break;
                            case 3:
                                prior3++;
                                break;
                        }
                        counts_before_me++;
                    } catch (Exception e) {
                        p_errors++;
                    }
                }
                final int f_pr1 = prior1, f_pr2 = prior2, f_pr3 = prior3, f_err = p_errors, all_z = abits.size(), f_before_me = counts_before_me;
                final String temp_upd = last_upd, f_budgets = budgets;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        all_zayav_view.setText(String.valueOf(all_z));
                        before_me_view.setText(String.valueOf(f_before_me));
                        prior1_view.setText(String.valueOf(f_pr1));
                        prior2_view.setText(String.valueOf(f_pr2));
                        prior3_view.setText(String.valueOf(f_pr3));
                        prior_err_view.setText(String.valueOf(f_err));
                        if (f_err > 0) prior_err_view.setTextColor(Color.RED);
                        TextView last_upd_v = findViewById(R.id.textView_last_upd);
                        last_upd_v.setText(temp_upd);
                        last_upd_v.setVisibility(View.VISIBLE);
                        TextView view_budget = findViewById(R.id.textView_budget);
                        view_budget.setText(f_budgets);
                    }
                });
            }
        }).start();
    }

    protected void preview_year(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int[] priors = new int[10];
                try {
                    Document doc = null;
                    Elements abits = null;
                    boolean f = true;
                    for (int page = 1; f; page++) {
                        doc = get_document((url + String.valueOf(page)));
                        abits = doc.select("tr.statement-zar");
                        for (Element abit : abits) {
                            if (abit.html().contains("контракт")) {
                                f = false;
                                break;
                            }
                            Element temp = abit.getElementsByTag("td").next().next().first();
                            if("0123456789".contains(temp.text())) {
                                int prior = Integer.parseInt(temp.text());
                                priors[prior]++;
                            }
                        }
                    }
                }catch (Exception e){
                }
                final int[] f_priors = priors;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder s=new StringBuilder();
                        for(int i=1;i<10;i++) s.append("П"+i+": "+f_priors[i]+" ");
                        TextView pr_y = findViewById(R.id.textView_prev_year);
                        pr_y.setText(s.toString());
                    }
                });
            }
        }).start();
    }

    public Document get_document(String url) {
        Document doc = null;
        do
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
            }
        while (doc == null);
        return doc;
    }

}
