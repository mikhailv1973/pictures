package ru.sibsoft.mikhailv.pictures;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikhail on 2/18/2016.
 */
public class BookFilterFavourites extends BookVirtual {

    List<PageVirtual> pages;

    public BookFilterFavourites(Book book) {
        super(book);
        pages = new ArrayList<PageVirtual>();
    }

    @Override
    protected void initializePages() {
        for(int i = 0, number = 0, count = underlyingBook.getCount(); i < count; i++) {
            if(underlyingBook.getPage(i).isFavourite()) {
                pages.add(new PageVirtual(underlyingBook.getPage(i), number++));
            }
        }
    }

    @Override
    public Page getPage(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public int getItemPosition(Object object) {
        try {
            PageVirtual page = getPageFromObject(object, PageVirtual.class);
            return underlyingBook.getCount() > 0 && page.isFavourite()
                    ? page.getNumber()
                    : POSITION_NONE;
        } catch(ClassCastException e) {
            return POSITION_NONE;
        }
    }
}
