
import java.io.PrintWriter;

import com.vladium.utils.IObjectProfileNode;
import com.vladium.utils.ObjectProfileFilters;
import com.vladium.utils.ObjectProfileVisitors;
import com.vladium.utils.ObjectProfiler;

// ----------------------------------------------------------------------------
/**
 * A small demo class to show how to use ObjectProfiler and related classes.
 * 
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 */
public class Main
{
    // public: ................................................................
    
    public static void main (final String [] args)
    {
        Object obj = new String [] {new String ("JavaWorld"),
                                    new String ("JavaWorld")};
        
        IObjectProfileNode profile = ObjectProfiler.profile (obj);
        
        System.out.println ("obj size = " + profile.size () + " bytes");
        System.out.println (profile.dump ());
        System.out.println ();
        
        // dump the same profile, but now only show nodes that are at least
        // 25% of 'obj' size:
        
        System.out.println ("size fraction filter with threshold=0.25:");
        final PrintWriter out = new PrintWriter (System.out);
        
        profile.traverse (ObjectProfileFilters.newSizeFractionFilter (0.25),
            ObjectProfileVisitors.newDefaultNodePrinter (out, null, null, true));
    }
    
    // protected: .............................................................

    // package: ...............................................................
    
    // private: ...............................................................

} // end of class
// ----------------------------------------------------------------------------