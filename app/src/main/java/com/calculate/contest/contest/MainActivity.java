package com.calculate.contest.contest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sPref;
    final String contest_score = "181.458", my_url = "";
    EditText ed_score, ed_myurl;
    TextView prior1_view, prior2_view, prior3_view, contract_view, all_zayav_view, before_me_view, view_err, view_last_year,
            textview_last_p1, last_upd_v,view_budget;
    RadioGroup radio_group;
    CheckBox checkbox_last_year;
    Switch switch_abit;
    final String[][] urls = {{"https://vstup.osvita.ua/r21/92/460953/","https://abit-poisk.org.ua/rate2018/direction/460953/?page=","https://abit-poisk.org.ua/rate2017/direction/46784/?page="},
            {"https://vstup.osvita.ua/r21/92/461081/","https://abit-poisk.org.ua/rate2018/direction/461081/?page=","https://abit-poisk.org.ua/rate2017/direction/46789/?page="},
            {"https://vstup.osvita.ua/r21/227/445717/","https://abit-poisk.org.ua/rate2018/direction/445717/?page=","https://abit-poisk.org.ua/rate2017/direction/44835/?page="}};
    int url_id = 0;
    ParserContest parser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed_score = findViewById(R.id.editText_score);
        prior1_view = findViewById(R.id.textView_prioritet1);
        prior2_view = findViewById(R.id.textView_prioritet2);
        prior3_view = findViewById(R.id.textView_prioritet3);
        contract_view = findViewById(R.id.textView_contract);
        all_zayav_view = findViewById(R.id.textView_all_zayav);
        before_me_view = findViewById(R.id.textView_before);
        last_upd_v = findViewById(R.id.textView_last_upd);
        ed_myurl = findViewById(R.id.editText_my_url);
        view_err = findViewById(R.id.textView_errors_count);
        radio_group = findViewById(R.id.radioGroup);
        checkbox_last_year = findViewById(R.id.checkBox);
        view_last_year = findViewById(R.id.textView_prev_year);
        textview_last_p1 = findViewById(R.id.textView_prohod);
        view_budget = findViewById(R.id.textView_budget);
        switch_abit = findViewById(R.id.switch_abit);
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_pi:
                        url_id = 0;
                        checkbox_last_year.setVisibility(View.VISIBLE);
                        checkbox_last_year.setChecked(true);
                        view_last_year.setVisibility(View.VISIBLE);
                        ed_myurl.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButton_kn:
                        url_id = 1;
                        checkbox_last_year.setVisibility(View.VISIBLE);
                        checkbox_last_year.setChecked(true);
                        view_last_year.setVisibility(View.VISIBLE);
                        ed_myurl.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButton_hneu_pi:
                        url_id = 2;
                        checkbox_last_year.setVisibility(View.VISIBLE);
                        checkbox_last_year.setChecked(true);
                        view_last_year.setVisibility(View.VISIBLE);
                        ed_myurl.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButton_mylink:
                        url_id = 3;
                        checkbox_last_year.setChecked(false);
                        checkbox_last_year.setVisibility(View.INVISIBLE);
                        view_last_year.setVisibility(View.INVISIBLE);
                        ed_myurl.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    int errors_pars = 0;

    public void onClick_parse(View v) {
        errors_pars = 0;
        if(url_id==3) Parsing(ed_myurl.getText().toString());
        else Parsing(urls[url_id][switch_abit.isChecked()?1:0]);
        if (checkbox_last_year.isChecked()) preview_year(urls[url_id][2]);
    }

    void saveData() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(contest_score, ed_score.getText().toString());
        ed.putString(my_url, ed_myurl.getText().toString());
        ed.commit();
    }

    void loadData() {
        sPref = getPreferences(MODE_PRIVATE);
        ed_score.setText(sPref.getString(contest_score, "181.458"));
        ed_myurl.setText(sPref.getString(my_url, ""));
    }

    private void Parsing(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                double my_score = Double.parseDouble(ed_score.getText().toString());
                parser = switch_abit.isChecked()? new ParserAbitsPoisk(url,my_score):new ParserOsvita(url,my_score);
                parser.Parsing();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view_err.setText(String.valueOf(parser.getErrors_parse()));
                        if(parser.getErrors_parse()>0)view_err.setTextColor(Color.RED);
                        all_zayav_view.setText(String.valueOf(parser.getAll_zayav()));
                        before_me_view.setText(String.valueOf(parser.getCounts_before_me()));
                        contract_view.setText(String.valueOf(parser.priors[0]));
                        prior1_view.setText(String.valueOf(parser.priors[1]));
                        prior2_view.setText(String.valueOf(parser.priors[2]));
                        prior3_view.setText(String.valueOf(parser.priors[3]));
                        last_upd_v.setText(parser.getLast_upd());
                        last_upd_v.setVisibility(View.VISIBLE);
                        view_budget.setText(String.valueOf(parser.budget));
                        textview_last_p1.setText(String.valueOf(parser.getProhod()));
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
                        if (abits == null || abits.size() == 0) {
                            f = false;
                            break;
                        }
                        for (Element abit : abits) {
                            f = !(abit.html().contains("контракт"));
                            Element temp = abit.getElementsByTag("td").next().next().first();
                            if ("0123456789".contains(temp.text())) {
                                int prior = Integer.parseInt(temp.text());
                                priors[prior]++;
                            }
                        }
                    }
                } catch (Exception e) {
                }
                final int[] f_priors = priors;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder s = new StringBuilder();
                        for (int i = 1; i < 10; i++) s.append("П" + i + ": " + f_priors[i] + " ");
                        TextView pr_y = findViewById(R.id.textView_prev_year);
                        pr_y.setText(s.toString());
                    }
                });
            }
        }).start();
    }

    public Document get_document(String url) throws InterruptedException {
        Document doc = null;
        do
            try {
                doc = Jsoup.connect(url).get();
            } catch (Exception e) {
                errors_pars++;
                final int temp_err = errors_pars;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view_err.setTextColor(Color.RED);
                        view_err.setText(String.valueOf(temp_err));
                    }
                });
                sleep(1000);
            }
        while (doc == null);
        return doc;
    }

}
