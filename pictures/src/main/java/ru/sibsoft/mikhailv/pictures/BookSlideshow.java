package ru.sibsoft.mikhailv.pictures;

/**
 * Created by Mikhail on 2/22/2016.
 */
public class BookSlideshow extends BookVirtual {

    boolean randomOrder = false;

    public BookSlideshow(Book book, boolean randomOrder) {
        super(book);
        this.randomOrder = randomOrder;
    }

    @Override
    public Page getPage(int position) {
        int underlying = randomOrder
                ? (int)Math.floor(Math.random() * underlyingBook.getCount())
                : position % underlyingBook.getCount();
        return new PageVirtual(underlyingBook.getPage(underlying), position);
    }

    @Override
    public int getCount() {
        return underlyingBook.getCount() > 1
                ? Integer.MAX_VALUE
                : underlyingBook.getCount();
    }

    @Override
    protected void initializePages() {
    }

    @Override
    public int getItemPosition(Object object) {
        try {
            PageVirtual page = getPageFromObject(object, PageVirtual.class);
            return underlyingBook.getCount() > 0 && (randomOrder || page.getNumber() % underlyingBook.getCount() == page.getRootPage().getNumber())
                    ? page.getNumber()
                    : POSITION_NONE;
        } catch(ClassCastException e) {
            return POSITION_NONE;
        }
    }
}
