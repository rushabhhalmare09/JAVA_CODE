A]Core Java
   1] Core Java
   2] Collections
   3] Java 8
   4] Java Memory Model
   5] Concurrency
   6] Garbage Collection
   7] JVM Internals
B]
C]
D]
E]



A]Core Java: -

   A.1]Core Java: -
   
This page focuses on oddities of Core Java from an interview perspective. For basic topics like inheritance, interfaces, etc. please read the book mentioned below.

Default init values: - 
   For fields (class level variables), values are auto assigned default values.
   Method local variables should be manually assigned.
   Default values (references = null, primitives = 0, boolean = false)
   Arrays initialize its elements: int[] numbers = new int[10]; will assign all ints in the array to 0.

String pool: -
  String constants are placed in a memory pool
  When retrieved, returns reference to string in the pool.
  Pool saves memory. New string constants with same value share same instance in the pool.
  String is immutable thus these values are never changed. For any updates, new string constant is created.
  String s = "abc" will place "abc" in pool and return its reference.
  String s = new String("abc") will also place "abc" in pool, as well as allocate new memory.

   JVM Internals: -
     Some of the topics listed below are tricks JVM uses to improve performance. Subset of these can be exploited to further improve application performance.
       Note: These topics are highly unlikely to come up in an interview. Feel free to just glance through without digging deep.
     Compressed pointers: -
        32 bit references can address 4GB, while 64 bit can reference 2^64 bytes (though limited by OS/RAM on the machine).
        Having 64 bit reference for every object increases memory usage. JVMs use compressed pointers to address this issue. (For more details: -> https://wiki.openjdk.java.net/display/HotSpot/CompressedOops)
        Basic idea is to store 32-bits per reference and then add to a base address to find final 64-bit address.
        Flag: -XX:+UseCompressedOops. Latest versions of 64-bit Java have this argument by default.
        Addresses upto 4gb untranslated.
        Addresses 4gb to 28gb, remove 3bits, because Java has 8 byte word aligned, thus 3 bits need not be stored.
        Important because Java has more references. In C++ memory layout follows struct layout.
        Java 8 has JVM args, +XX:ObjectAlignmentInBytes=16 for heap between 32gb and 64gb. 

String interning: -
Interning = storing strings in a pool and re-using them
If you intern a set of all strings, you can compare them by == improving performance.
It is stored internally as a hashmap (it is native C code, not Java code).
More details - [https://java-performance.info/string-intern-in-java-6-7-8/
and   https://java-performance.info/changes-to-string-java-1-7-0_06/]
                
How would you implement your own string interning?

private static final WeakHashMap<String, WeakReference<String>> s_manualCache
      = new WeakHashMap<String, WeakReference<String>>(100000);
 
private static String manualIntern(final String str){
    final WeakReference<String> cached = s_manualCache.get(str);
    if (cached != null){
        final String value = cached.get();
        if (value != null)
            return value;
    }
    s_manualCache.put(str, new WeakReference<String>(str));
    return str;
}

Thread Affinity: -
Makes the thread stick to a CPU core, even if it has no tasks left to perform. Unlike normal threads, this won't go into sleep/wait. Thread performs busy-spin. Note: This is wasting of CPU resources, and it can lead to thread starvation since other threads do not get access to that core. Thus, it needs to be used for the right applications. Helpful in latency critical applications like FX Trading.

Thread affinity only works for Linux and there are Java libraries available [ https://github.com/OpenHFT/Java-Thread-Affinity] to use the same.

Other topics: -
How does default hashCode method work? -- { https://varoa.net/jvm/java/openjdk/biased-locking/2017/01/30/hashCode.html}

What is Biased locking  -- { https://blogs.oracle.com/}

JVM Threads link with OS threads -- {https://openjdk.org/groups/hotspot/docs/RuntimeOverview.html#Thread%20Management%7Coutline}

Class Loaders  -- {https://www.jrebel.com/blog}

Memory consumptions of primitives and boxed variables  -- {https://java-performance.info/overview-of-memory-saving-techniques-java/}

Hoisting variables: JVM can hoist variables out of for loops to improve performance. {example - https://stackoverflow.com/questions/9338180/why-hotspot-will-optimize-the-following-using-hoisting/9338302#9338302 }

Escape analysis: JVM can choose to place a method local object (if it never escapes the method) in Thread-stack instead of heap. Improves performance since that object doesn't go through GC (can be just deleted once method completes).

Java Memory Model: - 
  Resources - https://vimeo.com/181788144

What is it? : - 
   Specification deciding how JVM can reorder instructions (for performance) aka ensures guaranteed ordering of of reads and writes under certain conditions (happens-before). Every JVM has to implement this spec.
   Barriers that forbid reordering instructions (load-load, load-store, store-load, store-store)
   Variables
   volatile
   final = all writes before volatile write will be reflected when/after volatile is read (potentially by other thread). Threads need to use the same volatile variable for this to work. For double/long (which occupy multiple word spaces, word-breakdown is forbidden to ensure integrity of data).
   Methods - synchronized
   Locks - normal objects used as locks, and lock classes like ReadWriteLock.
   Threads - When a new thread is started, it is guaranteed to see all values written before thread started.

Wrapper class pool: -
   Boolean
   Byte
   Character from \u0000 to \u007f (7f is 127 in decimal)
   Short and Integer from –128 to 127

Singleton options: -
   Using: static final variable (init guarantee)
   Using: Lazy loading (double checked)
   Using: Enums (by default lazy, and init guarantee)

Override method rules: -
   Same method name and parameter types
   Same or a subset of super methods' checked exceptions
   Any number of runtime exceptions
   Same or covariant return type

Covariant variables: -
   Variable types which are compatible.
   Eg: an int is covariant of long
   Eg: an Lion class is covariant of Animal class (only if Lion extends Animal)
   Can be used in parameters, return types or assignments

Varargs, boxing, widening: -
   Primitive Widening > Boxing > Varargs. Example.
   Widening then Boxing not allowed.
   Boxing then Widening allowed.
   Widening between wrapper classes not allowed (eg: Long n = new Integer(10); not allowed)

Inner classes: -
Personally I find this part of Java to be super annoying, unnecessary and hardly ever used in real-life (especially after Java 8). Also, this topic does not come up a lot in interviews, so just skimp through.
   Inner class: Can access enclosing class's variables (even private ones)
   Method local inner class: Same as above. Plus, it can access final variables in encapsulating method.
   Anonymous inner class: Just no name, otherwise same as above.
   Static inner class: No special relationship with outer class.
   
Reference types: -
   Weak reference - Eligible for GC if object not referenced by any other variables. Good for caches. Are enqueued in ReferenceQueue just before GC (object can be resurrected in finalize). Returns null once object is eligible for GC, even if object is resurrected, the weak-reference still is dead, and will return null.

Cloning: -
   clone method (protected) of Object class returns shallow copy. Need to be explicitly cast back.
   Requires class to implement Cloneable marker interface. Else returns CloneNotSupportedException
   Singletons should override clone method and throw CloneNotSupportedException.
     toString: -
         clone: -
               Cloneable interface. Its a mixin interface. Does not have clone method.
               Object class's clone method is protected
               Atypical - Presence of Colenable modifies behavior of Object.clone() behavior. If present it returns object which is field by field copy, and if not present, then .clone method throws 
                          CloneNotSupportedException
               Cloning is not done using constructor
               If you override clone do return super.clone(), if all classes do that up the chain, then Object.clone will be called and you will get the right copy
               This is important because spec doesnt enforce anything from Cloneable interface. So someone might override clone and not clone, nor call super.clone, causing problems
               Note: Objects.clone() creates a shallow copy
               If you override and write clone, ofcourse you cannot set final field, thus need to remove final modifiers
               Object's clone method is declared to throw CloneNotSupportedException, but overriding clone methods can omit this declaration.
               Like constructor, clone method should not call non-final methods, because super object might not be properly constructed yet, causing some data corruption
               Clone method must be synchronized in case of concurrency
               In short, you are better off, creating and using a copy-constructor.
               
======================================================================================================

A.2] Collections: -

Resources: -
   OCA/OCP Java SE 7 Programmer (book) - https://www.amazon.com/Programmer-Study-1Z0-803-1Z0-804-Certification/dp/0071772006/ref=asap_bc?ie=UTF8
   Cheat sheet (PDF)                   - http://files.zeroturnaround.com/pdf/zt_java_collections_cheat_sheet.pdf
   Effective Java study notes         
   
Table of contents: -
   Lists -
      ArrayList
      LinkedList
      Stack
      Vector
      CopyOnWriteArrayList
      Collections.synchronizedList

Lists: -

   ArrayList: -
      Backed by array (which are co-located in memory), thus fast iteration and get(i) operation.
      Slow inserts when the backed array is full and has to double in size.
      Fail-fast iterators, which can throw ConcurrentModificationException.
      Add is O(n) - When element is added to middle of list, all elements on the right have to be moved.
      Use Case - When iterations outnumber number of read/writes.
      
   LinkedList: - 
      Chain of nodes referencing each other (doubly linked list).
      No co-location of nodes, pointers need to be chased for next element, thus slow iterations and get(i) operation.
      Fail-fast iterators, which can throw ConcurrentModificationException.
      Implements Queue interface, thus allows offer/pop/peek operations.
      Add is O(1) - Adding element in middle of list is just adjusting the node pointers.
      Internally uses references (~ to skiplist) to optimize iterations.
      Use Case - Lot of inserts in middle of the list.
               
   Stack: -
      For stack operations push/pop/peek.
      Not used anymore. Recommended to use Deque implementations.

   Vector: -
      Synchronized version of list.
      Not used anymore. Recommended below mentioned alternatives.
      
   CopyOnWriteArrayList: -
      Thread-safe.
      Backed array is copied during every element insert.
      Avoids ConcurrentModificationException since iteration can continue in original copy, and insert results in new copy.
      High memory usage (more pressure on GC) due to the resulting copies.
      Use case - Large number of threads for read, low number of writes.
      
   Collections.synchronizedList: -
      Thread-safe.
      Can be slow due to mutual exclusion.
      Iterations have to be externally synchronized by developer
      Can throw ConcurrentModificationException if (above mentioned) synchronization not done during iteration.


Sets: -
   Collection of unique elements. No duplicates.

   HashSet: -
      Backed by HashMap.
      Performance can vary based on hashCode implementation.
      Constant time get/remove/add/contains (subject to above point).
      Fail-fast iterators.
      Insertion order not retained.
   
   LinkedHashSet" -
      Insertion order is retained.
      Uses doubly-linked list to maintain the order.
      Iteration can be slower due to this.
      Other features, same as HashSet above (except iteration).
   
   TreeSet: -
      Elements sorted by their natural order (or Comparator passed in constructor).
      Log(n) time for add/remove/contains operations.
      Navigable (floor, ceiling, higher, lower, headSet, tailSet operations).
      Fail fast iterators.
   
   ConcurrentSkipListSet: -
      Thread-safe.
      Log(n) time for add/remove/contains operations.
      Navigable (floor, ceiling, higher, lower, headSet, tailSet operations).
      Size method is not constant time operation.
      Weakly consistent iterators (do not throw ConcurrentModificationException but also may not reflect concurrently added items).
      Thus, bulk operations (addAll, removeAll, retainAll, containsAll etc) are not guaranteed to be atomic.

   CopyOnWriteArraySet: -
      Backed by CopyOnWriteArrayList
      Thread-safe.
      Slow. Operations have to iterate through the array for most operations.
      Recommended where reads vastly outnumber writes and set size is small.
      
   EnumSet:- 
      To be used with Enum types.
      Very efficient and fast (backed by bit-vectors).
      Weakly consistent iterators.
      Nulls not allowed.

Maps: -
   HashMap: -
      key, value pairs.
      Permits a null key, and null values.
      Iteration order not guaranteed.
      Throws ConcurrentModificationException.
      Article for details on implementation - https://deepakvadgama.com/blog/java-hashmap-internals/
   
   HashMap implementation details: -
      Backed by array (buckets), array-size is known as table-size.
      Position in array = element-hash % table-size.
      If elements end up in same bucket, they are added to linked-list (or a balanced red-black tree).
      O(1) access (if hashcode properly distributes the values, else O(n) for linked-list & O(log(n)) for tree.
      Load factor - 0.75 default, decides when table-size should increase (double).
      Bigger load-factor - more space-efficient, reduced speed (due to more elements in same bucket).
      Lower load-factor - less space-efficient, more speed (less, ideally 1 element in 1 bucket).
      Initial table-size = 16.
      
   LinkedHashMap: -
      Insertion order is retained.
      
   Hashtable: -
      Thread-safe.
      Not used anymore, ConcurrentHashMap recommended.
      
   ConcurrentHashMap: -
      Thread-safe.
      Fine grained locking called striped locking (map is divided into segments, each with associated lock. Threads holding different locks don't conflict).
      Improved performance over Hashtable.
      
   TreeMap: -
      Sorted by keys.
      Uses Red-Black tree implementation.
      
   ConcurrentSkipListMap: -
      Thread-safe version of TreeMap.
      Navigable (floor, ceiling, higher, lower, headSet, tailSet operations).
