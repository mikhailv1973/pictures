package ru.sibsoft.mikhailv.pictures;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mikhail on 2/18/2016.
 */
public class BookLoop extends BookVirtual {

    private List<PageVirtual> pages;
    private PageVirtual pagePrimary = null;

    public BookLoop(Book book) {
        super(book);
    }

    @Override
    public int getCount() {
        int count = underlyingBook.getCount();
        return count > 1 ? count + 2 : count;
    }

    @Override
    protected void initializePages() {
        pages = new ArrayList<PageVirtual>();
        for(int i = 0, count = getCount(); i < count; i++) {
            pages.add(new PageVirtual(underlyingBook.getPage(i % underlyingBook.getCount()), i));
        }
    }

    @Override
    public Page getPage(int position) {
        return pages.get(position).WithNumber(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        pagePrimary = getPageFromObject(object, PageVirtual.class);
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

    private boolean isCorrectNumber(PageVirtual page) {
        return page.getNumber() < getCount() &&
                page.getNumber() % underlyingBook.getCount() == page.getUnderlyingPage().getNumber();
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if(getCount() < 2) {
            return;
        }
        int primaryNumber = isCorrectNumber(pagePrimary)
                ? pagePrimary.getNumber()
                : pagePrimary.getUnderlyingPage().getNumber();
        int newPrimaryNumber = primaryNumber == getCount() - 1 ? 1
                : primaryNumber == 0 ? getCount() - 2
                : primaryNumber;
        boolean needNotify = pagePrimary.getNumber() != newPrimaryNumber;
        pagePrimary.setNumber(newPrimaryNumber);
        Set<Integer> onScreen = new HashSet<>();
        onScreen.add(pagePrimary.getUnderlyingPage().getNumber());
        for(int i = 0; i < container.getChildCount(); i++) {
            try {
                PageVirtual page = getPageFromObject(container.getChildAt(i), PageVirtual.class);
                if(page == pagePrimary) {
                    continue;
                } else if(onScreen.contains(page.getUnderlyingPage().getNumber())) {
                    needNotify = true;
                    page.setNumber(POSITION_NONE);
                    continue;
                }
                onScreen.add(page.getUnderlyingPage().getNumber());
                int number = isCorrectNumber(page)
                        ? page.getNumber()
                        : page.getUnderlyingPage().getNumber();
                int newNumber = newPrimaryNumber == 1 && number == getCount() - 2 ? 0
                        : newPrimaryNumber == getCount() - 2 && number == 1 ? getCount() - 1
                        : number;
                needNotify = needNotify || page.getNumber() != newNumber;
                page.setNumber(newNumber);
            } catch(ClassCastException e) {
            }
        }
        if(needNotify) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        try {
            PageVirtual page = getPageFromObject(object, PageVirtual.class);
            return underlyingBook.getCount() > 0 && (page.getNumber() % underlyingBook.getCount()) == page.getRootPage().getNumber()
                    ? page.getNumber()
                    : POSITION_NONE;
        } catch(ClassCastException e) {
            return POSITION_NONE;
        }
    }
}
