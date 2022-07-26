// 
// Decompiled by Procyon v1.0-SNAPSHOT
// 

package cl.uai.webcursos.emarking.desktop.data;

import java.util.Iterator;
import java.text.DecimalFormat;
import java.io.File;
import cl.uai.webcursos.emarking.desktop.FixRowDialog;
import java.awt.Component;
import javax.swing.JOptionPane;
import cl.uai.webcursos.emarking.desktop.EmarkingDesktop;
import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import java.util.Hashtable;

public class Pages extends Hashtable<Integer, Page>
{
    private static final long serialVersionUID = -8592122074506658642L;
    private static Logger logger;
    private Moodle moodle;
    private int max;
    private int min;
    private List<Integer> studentIds;
    private Statistics stats;
    
    static {
        Pages.logger = Logger.getLogger(Pages.class);
    }
    
    public Pages(final Moodle _moodle) {
        this.max = -1;
        this.min = Integer.MAX_VALUE;
        this.studentIds = new ArrayList<Integer>();
        this.moodle = _moodle;
    }
    
    public Statistics getStats() {
        return this.stats;
    }
    
    public int getMaxPageNumber() {
        return this.max;
    }
    
    public boolean fixPageData(final int row, final JFrame frame) throws Exception {
        if (this.moodle.getQrExtractor().isDoubleside() && row % 2 != 0) {
            JOptionPane.showMessageDialog(null, EmarkingDesktop.lang.getString("onlyevenrowsdoubleside"));
            return false;
        }
        final Page current = this.get(row);
        final FixRowDialog dialog = new FixRowDialog(this.moodle);
        dialog.getLblPageNumber().setText(Integer.toString(row + 1));
        if (current.getCourse() != null) {
            dialog.getCoursesCombo().setSelectedItem(current.getCourse());
        }
        if (current.getStudent() != null) {
            dialog.getStudentsCombo().setSelectedItem(current.getStudent());
        }
        if (current.getPagenumber() > 0) {
            dialog.getComboPageNumber().setSelectedIndex(current.getPagenumber());
        }
        dialog.setModal(true);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        if (dialog.isCancelled()) {
            return false;
        }
        final Page newpage = new Page(this.moodle);
        newpage.setStudent((Student)dialog.getStudentsCombo().getSelectedItem());
        newpage.setCourse((Course)dialog.getCoursesCombo().getSelectedItem());
        newpage.setPagenumber(dialog.getComboPageNumber().getSelectedIndex());
        this.renamePage(current, newpage, newpage.getPagenumber());
        return true;
    }
    
    public void fixFromPrevious(final int row) throws Exception {
        if (this.moodle.isDoubleside() && row % 2 != 0) {
            throw new Exception("Invalid row number for fixing row in doubleside");
        }
        final int minimum = this.moodle.isDoubleside() ? 2 : 1;
        if (row < minimum) {
            throw new Exception("There is no previous row to copy from");
        }
        final Page previous = this.get(row - 1);
        final Page current = this.get(row);
        if (previous == null || current == null) {
            throw new Exception("There is no information for previous or current pages. This is a fatal error, please notify the author.");
        }
        if (previous.getStudent() == null || previous.getCourse() == null) {
            throw new Exception("There is no student or course information in the page you want to copy from");
        }
        this.renamePage(current, previous, previous.getPagenumber() + 1);
    }
    
    public void fixFromFollowing(final int row) throws Exception {
        if (this.moodle.isDoubleside() && row % 2 != 0) {
            throw new Exception("Invalid row number for fixing row in doubleside");
        }
        if (row > this.size() - 2) {
            throw new Exception("There is no following row to copy from");
        }
        final int gap = this.moodle.isDoubleside() ? 2 : 1;
        final Page following = this.get(row + gap);
        final Page current = this.get(row);
        if (following == null || current == null) {
            throw new Exception("There is no information for previous or current pages. This is a fatal error, please notify the author.");
        }
        if (following.getStudent() == null || following.getCourse() == null) {
            throw new Exception("There is no student or course information in the page you want to copy from");
        }
        if (following.getPagenumber() <= 1) {
            throw new Exception("The page you want to precede is the first one");
        }
        this.renamePage(current, following, following.getPagenumber() - 1);
    }
    
    private void renamePage(final Page current, final Page copyfrom, final int newpagenumber) throws Exception {
        final int row = current.getRow();
        if (this.max > 0 && newpagenumber > this.max) {
            Pages.logger.warn("Invalid page number, exceeds maximum");
        }
        String oldfilename = current.getFilename();
        File oldfile = new File(String.valueOf(this.moodle.getQrExtractor().getTempdirStringPath()) + "/" + oldfilename + ".png");
        String newfilename = String.valueOf(copyfrom.getStudent().getId()) + "-" + copyfrom.getCourse().getId() + "-" + newpagenumber;
        File newfile = new File(String.valueOf(this.moodle.getQrExtractor().getTempdirStringPath()) + "/" + newfilename + ".png");
        if (newfile.exists()) {
            throw new Exception("Invalid fix, page already exists!");
        }
        oldfile.renameTo(newfile);
        oldfile = new File(String.valueOf(this.moodle.getQrExtractor().getTempdirStringPath()) + "/" + oldfilename + "_a.png");
        oldfile.renameTo(new File(String.valueOf(this.moodle.getQrExtractor().getTempdirStringPath()) + "/" + newfilename + "_a.png"));
        if (current.getStudent() != null) {
            current.getStudent().removePage(current);
        }
        copyfrom.getStudent().addPage(current);
        current.setStudent(copyfrom.getStudent());
        current.setCourse(copyfrom.getCourse());
        current.setPagenumber(newpagenumber);
        current.setFilename(newfilename);
        Pages.logger.debug("Changing " + oldfilename + " to " + newfilename);
        this.updateStats(current);
        if (this.moodle.getQrExtractor().isDoubleside()) {
            final Page next =this.get(row + 1);
            oldfilename = next.getFilename();
            oldfile = new File(String.valueOf(this.moodle.getQrExtractor().getTempdirStringPath()) + "/" + oldfilename + ".png");
            newfilename = String.valueOf(newfilename) + "b";
            newfile = new File(String.valueOf(this.moodle.getQrExtractor().getTempdirStringPath()) + "/" + newfilename + ".png");
            if (newfile.exists()) {
                throw new Exception("Invalid fix, page already exists!");
            }
            oldfile.renameTo(newfile);
            oldfile = new File(String.valueOf(this.moodle.getQrExtractor().getTempdirStringPath()) + "/" + oldfilename + "_a.png");
            oldfile.renameTo(new File(String.valueOf(this.moodle.getQrExtractor().getTempdirStringPath()) + "/" + newfilename + "_a.png"));
            if (next.getStudent() != null) {
                next.getStudent().removePage(next);
            }
            copyfrom.getStudent().addPage(next);
            next.setStudent(copyfrom.getStudent());
            next.setCourse(copyfrom.getCourse());
            next.setPagenumber(newpagenumber);
            next.setFilename(newfilename);
            Pages.logger.debug("Changing " + oldfilename + " to " + newfilename);
            this.updateStats(next);
        }
    }
    
    public boolean isNumberOutlier(final int pagenumber) {
        if (this.stats == null) {
            return false;
        }
        final int mean = (int)Math.rint(this.stats.getMean());
        final int stdev = (int)Math.rint(this.stats.getStdDev());
        final int minstat = mean - stdev;
        final int maxstat = mean + stdev;
        return pagenumber > maxstat || pagenumber < minstat;
    }
    
    public Object[] getRowData(final int row) {
        final Page p = this.get(row);
        if (p == null) {
            return null;
        }
        final Object[] rowData = { p.getRow() + 1, (p.getStudent() == null) ? EmarkingDesktop.lang.getString("nn") : p.getStudent().getFullname(), (p.getCourse() == null) ? EmarkingDesktop.lang.getString("notfound") : p.getCourse().getFullname(), p.getPagenumber() };
        return rowData;
    }

    public String getSummary() {
        String output = "";
        output += " " + EmarkingDesktop.lang.getString("pages") + ": " + this.keySet().size();
        output += " " + EmarkingDesktop.lang.getString("students") + ": " + studentIds.size() + "/" + this.moodle.getStudents().size();
        output += " " + EmarkingDesktop.lang.getString("pagesperstudent") + ":";
        DecimalFormat df = new DecimalFormat("#.##");
        if(this.stats != null) {
            output += " " + df.format(this.stats.getMean());
            output += " +- " + df.format(this.stats.getStdDev());
        }
        output += " [" + min + "-" + max + "]";
        return output;
    }

    @Override
    public synchronized Page put(final Integer key, final Page value) {
        this.updateStats(value);
        return super.put(key, value);
    }
    
    private void updateStats(final Page p) {
        if (p.getStudent() != null) {
            if (p.getStudent().getPages() > this.max) {
                this.max = p.getStudent().getPages();
            }
            if (p.getStudent().getPages() < this.min) {
                this.min = p.getStudent().getPages();
            }
            if (!this.studentIds.contains(p.getStudent().getId())) {
                this.studentIds.add(p.getStudent().getId());
            }
            final double[] data = new double[this.studentIds.size()];
            int current = 0;
            for (final int studentid : this.studentIds) {
                data[current] = this.moodle.getStudents().get(studentid).getPages();
                ++current;
            }
            this.stats = new Statistics(data);
        }
    }
}
