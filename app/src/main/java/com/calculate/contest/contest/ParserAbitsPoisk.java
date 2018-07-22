package com.calculate.contest.contest;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ParserAbitsPoisk extends ParserContest {
    public ParserAbitsPoisk(String Url, double Me_score) {
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
        Element zayav = doc.select("div.text-left > div.subhead-2 > div.body-2 > strong").first();
        this.all_zayav = Integer.parseInt(zayav.text().toString());
    }

    @Override
    protected void checkBudgets() {
        Element bud = doc.select("div.card-header > div.text-left > h2.headline > span.font300").first();
        StringBuilder temp = new StringBuilder(bud.text());
        temp.delete(0, temp.indexOf("БМmax") + 6);
        if(temp.indexOf(" ")>0) temp.delete(temp.indexOf(" "), temp.length());
        this.budget = Integer.parseInt(temp.toString());
    }

    @Override
    protected void checkLastUpd() throws InterruptedException {
        Document temp_doc = null;
        temp_doc = download_Document("https://abit-poisk.org.ua/rate-review/");
        StringBuilder temp = new StringBuilder(temp_doc.selectFirst("table[class=table table-bordered] > tbody > tr > td").text().toString());
        temp.delete(0, temp.indexOf("(") + 1);
        temp.delete(temp.indexOf(")"), temp.length());
        last_upd = temp.toString();
    }

    @Override
    protected void parseAbits() throws InterruptedException {
        balls_p1 = new ArrayList();
        int pages = counts_pages();
        boolean f_more = true;
        for (int i = 1; i <= pages && f_more; i++) {
            if(i!=1)doc = download_Document((url + String.valueOf(i)));
            abits = doc.select("table[class=table table-bordered table-hover] > tbody > tr[class*=application-status]");
            for (Element abit : abits) {
                Elements temp_info = abit.getElementsByTag("td");
                double score = Double.parseDouble(temp_info.eq(3).text());
                int prior_t = getPrior(temp_info.eq(2).text().toString());
                if(prior_t==1) balls_p1.add(new Double(score));
                if(me_score<score){
                    counts_before_me++;
                    priors[prior_t]++;
                }
                f_more = me_score<score;
            }
        }
    }

    protected int getPrior(String temp){
        int res=0;
        if ("123456789".contains(temp)) res = Integer.parseInt(temp);
        return res;
    }

    private int counts_pages() {
        Elements temps = doc.select("div.card-header > a[class*=btn btn-default ajax secondary-text]");
        return Integer.parseInt(temps.eq(temps.size() - 2).text());
    }

}
