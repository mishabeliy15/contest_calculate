package com.calculate.contest.contest;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;

import static java.util.Collections.sort;

public class ParserOsvita extends ParserContest {
    public ParserOsvita(String Url, double Me_score) {
        this.url = Url;
        this.me_score = Me_score;
        try {
            this.doc = download_Document(url);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    @Override
    protected void parseZayav() {
        Elements zayav = doc.select("div[class=table-of-specs-item panel-mobile]");
        StringBuilder temp = new StringBuilder(zayav.text());
        temp = temp.delete(0, temp.indexOf("Всього поданих заяв:"));
        for (int i = 0; i < 3; i++) temp = temp.delete(0, temp.indexOf(" ") + 1);
        temp = temp.delete(temp.indexOf(" "), temp.length());
        this.all_zayav = Integer.parseInt(temp.toString());
    }

    @Override
    protected void checkBudgets() {
        Elements bud = doc.select("div[class=table-of-specs-item panel-mobile]");
        StringBuilder temp = new StringBuilder(bud.text());
        temp = temp.delete(0, temp.indexOf("Максимальне держзамовлення:"));
        for (int i = 0; i < 2; i++) temp = temp.delete(0, temp.indexOf(" ") + 1);
        temp = temp.delete(temp.indexOf(" "), temp.length());
        this.budget = Integer.parseInt(temp.toString());
    }

    @Override
    protected void checkLastUpd() {
        this.last_upd = doc.selectFirst("div.last-update").text();
    }

    @Override
    protected void parseAbits() {
        this.abits = doc.select("table.rwd-table > tbody > tr[class*=rstatus]:not(.hdn)");
        balls_p1 = new ArrayList();
        for (Element abit : abits) {
            String temp;
            temp = abit.select("td[data-th=П]").first().text();
            int prior = !temp.contains("—") ? Integer.parseInt(temp) : 0;
            double score = Double.parseDouble(abit.select("td[data-th=Бал]").first().text());
            if (prior == 1) balls_p1.add(score);
            if (me_score < score) {
                counts_before_me++;
                priors[prior]++;
            }
        }
    }
}
