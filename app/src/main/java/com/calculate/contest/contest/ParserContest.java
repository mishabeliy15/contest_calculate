package com.calculate.contest.contest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Thread.sleep;
import static java.util.Collections.sort;

public abstract class ParserContest {
    protected String url = null, last_upd = null;
    protected int errors_parse = 0, budget = 0, counts_before_me = 0, all_zayav = 0;
    protected double me_score = 0;
    protected Document doc = null;
    protected Elements abits = null;
    public ArrayList<Double> balls_p1;
    public int[] priors = new int[9];

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getErrors_parse() {
        return errors_parse;
    }

    public String getLast_upd() {
        return last_upd;
    }

    public int getAll_zayav() {
        return all_zayav;
    }

    public int getCounts_before_me() {
        return counts_before_me;
    }

    public Elements getAbits() {
        return abits;
    }

    public void Parsing() {
        checkBudgets();
        parseZayav();
        try {
            checkLastUpd();
            parseAbits();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getProhod() {
        sort(balls_p1, Collections.<Double>reverseOrder());
        return balls_p1.get(budget <= balls_p1.size() ? (budget - 1) : balls_p1.size()-1);
    }

    protected abstract void parseZayav();

    protected abstract void checkLastUpd() throws InterruptedException;

    protected abstract void checkBudgets();

    protected abstract void parseAbits() throws InterruptedException;

    protected Document download_Document(String url) throws InterruptedException {
        Document doc_temp = null;
        do
            try {
                doc_temp = Jsoup.connect(url).get();
            } catch (Exception e) {
                errors_parse++;
                sleep(1000);
            }
        while (doc_temp == null);
        return doc_temp;
    }
}
