package pack;

public interface GSetups {

    /**
     * זו הפעולה שרצה בתכילת התוכנית, היא מיועדת להגדרת מאפייני המסך וקביעת משתנים התחלתיים
     */
    public void initialize();
    /**
     * הפעולה שרצה בכל מחזור של התוכנית
     */
    public void execute();
    /**
     * הפעולה האחרונה שצרוץ בתוכנית, תרוץ עם סגירת התוכנית
     */
    public void lastFunction();
    /**
     * פעולה שמטרתה לסגור את התוכנית אם מתקבל ערך בוליאני חיובי שהפעולה תחזיר, בברירת המחדל הפעולה תחזיר ערך בוליאני שלילי ובכך התוכנה לא תפסק
     */
    public boolean end();

    double[] getFpsArr();
    double currentFPS();
    void addGButton(GButton button);
    void addGTextField(GTextField textField);
    int xOnCanvas();
    int yOnCanvas();
    int xOnScreen();
    int yOnScreen();

}
