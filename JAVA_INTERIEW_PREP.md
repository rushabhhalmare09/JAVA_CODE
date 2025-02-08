
**A]Core Java**
- 1] Core Java
- 2] Collections
- 3] Java 8
- 4] Java Memory Model
- 5] Concurrency
- 6] Garbage Collection
- 7] JVM Internals

B]
C]
D]
E]



**A]Core Java: -**
=
   A.1]Core Java: -
   
This page focuses on oddities of Core Java from an interview perspective. For basic topics like inheritance, interfaces, etc. please read the book mentioned below.

Default init values: - 
   - For fields (class level variables), values are auto assigned default values.
   - Method local variables should be manually assigned.
   - Default values (references = null, primitives = 0, boolean = false)
   - Arrays initialize its elements: int[] numbers = new int[10]; will assign all ints in the array to 0.

String pool: -
  - String constants are placed in a memory pool
  - When retrieved, returns reference to string in the pool.
  - Pool saves memory. New string constants with same value share same instance in the pool.
  - String is immutable thus these values are never changed. For any updates, new string constant is created.
  - String s = "abc" will place "abc" in pool and return its reference.
  - String s = new String("abc") will also place "abc" in pool, as well as allocate new memory.

   **JVM Internals**:-
   - Some of the topics listed below are tricks JVM uses to improve performance. Subset of these can be exploited to further improve application performance.
   - Note: These topics are highly unlikely to come up in an interview. Feel free to just glance through without digging deep.
   - Compressed pointers: -
       - 32 bit references can address 4GB, while 64 bit can reference 2^64 bytes (though limited by OS/RAM on the machine).
       - Having 64 bit reference for every object increases memory usage. JVMs use compressed pointers to address this issue. (For more details: -> https://wiki.openjdk.java.net/display/HotSpot/CompressedOops)
       - Basic idea is to store 32-bits per reference and then add to a base address to find final 64-bit address.
       - Flag: -XX:+UseCompressedOops. Latest versions of 64-bit Java have this argument by default.
       - Addresses upto 4gb untranslated.
       - Addresses 4gb to 28gb, remove 3bits, because Java has 8 byte word aligned, thus 3 bits need not be stored.
       - Important because Java has more references. In C++ memory layout follows struct layout.
       - Java 8 has JVM args, +XX:ObjectAlignmentInBytes=16 for heap between 32gb and 64gb. 

String interning: -
  - Interning = storing strings in a pool and re-using them
  - If you intern a set of all strings, you can compare them by == improving performance.
  - It is stored internally as a hashmap (it is native C code, not Java code).
  - More details - [https://java-performance.info/string-intern-in-java-6-7-8/   and   https://java-performance.info/changes-to-string-java-1-7-0_06/]
                
**How would you implement your own string interning?**

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

**Thread Affinity: -**
  - Makes the thread stick to a CPU core, even if it has no tasks left to perform.
  - Unlike normal threads, this won't go into sleep/wait. Thread performs busy-spin.
  - Note: This is wasting of CPU resources, and it can lead to thread starvation since other threads do not get access to that core. Thus, it needs to be used for the right applications. Helpful in latency critical applications like FX Trading.

  - Thread affinity only works for Linux and there are Java libraries available [ https://github.com/OpenHFT/Java-Thread-Affinity] to use the same.

**Other topics: -**
  - How does default hashCode method work? -- { https://varoa.net/jvm/java/openjdk/biased-locking/2017/01/30/hashCode.html}
  - What is Biased locking  -- { https://blogs.oracle.com/}
  - JVM Threads link with OS threads -- {https://openjdk.org/groups/hotspot/docs/RuntimeOverview.html#Thread%20Management%7Coutline}
  - Class Loaders  -- {https://www.jrebel.com/blog}
  - Memory consumptions of primitives and boxed variables  -- {https://java-performance.info/overview-of-memory-saving-techniques-java/}

**Hoisting variables**: -
  - JVM can hoist variables out of for loops to improve performance. {example - https://stackoverflow.com/questions/9338180/why-hotspot-will-optimize-the-following-using-hoisting/9338302#9338302 }

**Escape analysis**: -
  - JVM can choose to place a method local object (if it never escapes the method) in Thread-stack instead of heap. Improves performance since that object doesn't go through GC (can be just deleted once method completes).

**Java Memory Model** : -
  - Resources - https://vimeo.com/181788144
  - **What is it?** : -
     - Specification deciding how JVM can reorder instructions (for performance) aka ensures guaranteed ordering of of reads and writes under certain conditions (happens-before). Every JVM has to implement this spec.
     - Barriers that forbid reordering instructions (load-load, load-store, store-load, store-store)
     - Variables
     - volatile
     - inal = all writes before volatile write will be reflected when/after volatile is read (potentially by other thread). Threads need to use the same volatile variable for this to work. For double/long (which occupy multiple word spaces, word-breakdown is forbidden to ensure integrity of data).
     - Methods - synchronized
     - Locks - normal objects used as locks, and lock classes like ReadWriteLock.
     - Threads - When a new thread is started, it is guaranteed to see all values written before thread started.

**Wrapper class pool**: -
  - Boolean
  - Byte
  - Character from \u0000 to \u007f (7f is 127 in decimal)
  - Short and Integer from –128 to 127

**Singleton options**: -
  - Using: static final variable (init guarantee)
  - Using: Lazy loading (double checked)
  - Using: Enums (by default lazy, and init guarantee)

**Override method rules**: -
   - Same method name and parameter types
   - Same or a subset of super methods' checked exceptions
   - Any number of runtime exceptions
   - Same or covariant return type

**Covariant variables**: -
  - Variable types which are compatible.
  - Eg: an int is covariant of long.
  - Eg: an Lion class is covariant of Animal class (only if Lion extends Animal)
  - Can be used in parameters, return types or assignments

**Varargs, boxing, widening**: -
  - Primitive Widening > Boxing > Varargs. Example.
  - Widening then Boxing not allowed.
  - Boxing then Widening allowed.
  - Widening between wrapper classes not allowed (eg: Long n = new Integer(10); not allowed)

**Inner classes**: -
  - Personally I find this part of Java to be super annoying, unnecessary and hardly ever used in real-life (especially after Java 8). Also, this topic does not come up a lot in interviews, so just skimp through.
    - Inner class: Can access enclosing class's variables (even private ones)
    - Method local inner class: Same as above. Plus, it can access final variables in encapsulating method.
    - Anonymous inner class: Just no name, otherwise same as above.
    - Static inner class: No special relationship with outer class.
   
**Reference types**: -
  - Weak reference - Eligible for GC if object not referenced by any other variables. Good for caches. Are enqueued in ReferenceQueue just before GC (object can be resurrected in finalize). Returns null once object is eligible for GC, even if object is resurrected, the weak-reference still is dead, and will return null.

**Cloning**: -
  - clone method (protected) of Object class returns shallow copy. Need to be explicitly cast back.
  - Requires class to implement Cloneable marker interface. Else returns CloneNotSupportedException
  - Singletons should override clone method and throw CloneNotSupportedException.
      - toString: -
        - clone: -
               - Cloneable interface. Its a mixin interface. Does not have clone method.
               - Object class's clone method is protected
               - Atypical - Presence of Colenable modifies behavior of Object.clone() behavior. If present it returns object which is field by field copy, and if not present, then .clone method throws CloneNotSupportedException
               - Cloning is not done using constructor
               - If you override clone do return super.clone(), if all classes do that up the chain, then Object.clone will be called and you will get the right copy
               - This is important because spec doesnt enforce anything from Cloneable interface. So someone might override clone and not clone, nor call super.clone, causing problems
               - Note: Objects.clone() creates a shallow copy
               - If you override and write clone, ofcourse you cannot set final field, thus need to remove final modifiers
               - Object's clone method is declared to throw CloneNotSupportedException, but overriding clone methods can omit this declaration.
               - Like constructor, clone method should not call non-final methods, because super object might not be properly constructed yet, causing some data corruption
               - Clone method must be synchronized in case of concurrency
               - In short, you are better off, creating and using a copy-constructor.
               
======================================================================================================

**A.2] Collections: -**

  Resources: -
  - OCA/OCP Java SE 7 Programmer (book) - https://www.amazon.com/Programmer-Study-1Z0-803-1Z0-804-Certification/dp/0071772006/ref=asap_bc?ie=UTF8
  - Cheat sheet (PDF)                   - http://files.zeroturnaround.com/pdf/zt_java_collections_cheat_sheet.pdf
  - Effective Java study notes         
   
  Table of contents: -
     Lists -
        ArrayList
        LinkedList
        Stack
        Vector
        CopyOnWriteArrayList
        Collections.synchronizedList

-------
Lists: -

   ArrayList: -
   - Backed by array (which are co-located in memory), thus fast iteration and get(i) operation.
   - Slow inserts when the backed array is full and has to double in size.
   - Fail-fast iterators, which can throw ConcurrentModificationException.
   - Add is O(n) - When element is added to middle of list, all elements on the right have to be moved.
   - Use Case - When iterations outnumber number of read/writes.
      
   LinkedList: - 
   - Chain of nodes referencing each other (doubly linked list).
   - No co-location of nodes, pointers need to be chased for next element, thus slow iterations and get(i) operation.
   - Fail-fast iterators, which can throw ConcurrentModificationException.
   - Implements Queue interface, thus allows offer/pop/peek operations.
   - Add is O(1) - Adding element in middle of list is just adjusting the node pointers.
   - Internally uses references (~ to skiplist) to optimize iterations.
   - Use Case - Lot of inserts in middle of the list.
               
   Stack: -
   - For stack operations push/pop/peek.
   - Not used anymore. Recommended to use Deque implementations.

   Vector: -
   - Synchronized version of list.
   - Not used anymore. Recommended below mentioned alternatives.
      
   CopyOnWriteArrayList: -
   - Thread-safe.
   - Backed array is copied during every element insert.
   - Avoids ConcurrentModificationException since iteration can continue in original copy, and insert results in new copy.
   - High memory usage (more pressure on GC) due to the resulting copies.
   - Use case - Large number of threads for read, low number of writes.
      
   Collections.synchronizedList: -
   - Thread-safe.
   - Can be slow due to mutual exclusion.
   - Iterations have to be externally synchronized by developer.
   - Can throw ConcurrentModificationException if (above mentioned) synchronization not done during iteration.

-------

Sets: -
  - Collection of unique elements. No duplicates.

   HashSet: -
   - Backed by HashMap.
   - Performance can vary based on hashCode implementation.
   - Constant time get/remove/add/contains (subject to above point).
   - Fail-fast iterators.
   - Insertion order not retained.
   
   LinkedHashSet" -
   - Insertion order is retained.
   - Uses doubly-linked list to maintain the order.
   - Iteration can be slower due to this.
   - Other features, same as HashSet above (except iteration).
   
   TreeSet: -
   - Elements sorted by their natural order (or Comparator passed in constructor).
   - Log(n) time for add/remove/contains operations.
   - Navigable (floor, ceiling, higher, lower, headSet, tailSet operations).
   - Fail fast iterators.
   
   ConcurrentSkipListSet: -
   - Thread-safe.
   - Log(n) time for add/remove/contains operations.
   - Navigable (floor, ceiling, higher, lower, headSet, tailSet operations).
   - Size method is not constant time operation.
   - Weakly consistent iterators (do not throw ConcurrentModificationException but also may not reflect concurrently added items).
   - Thus, bulk operations (addAll, removeAll, retainAll, containsAll etc) are not guaranteed to be atomic.

   CopyOnWriteArraySet: -
   - Backed by CopyOnWriteArrayList
   - Thread-safe.
   - Slow. Operations have to iterate through the array for most operations.
   - Recommended where reads vastly outnumber writes and set size is small.
      
   EnumSet:- 
   - To be used with Enum types.
   - Very efficient and fast (backed by bit-vectors).
   - Weakly consistent iterators.
   - Nulls not allowed.
      
-------
Maps: -
   
   HashMap: -
   - key, value pairs.
   - Permits a null key, and null values.
   - Iteration order not guaranteed.
   - Throws ConcurrentModificationException.
   - Article for details on implementation - https://deepakvadgama.com/blog/java-hashmap-internals/
   
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

Queues: -
   LinkedList: -
      Implements Queue interface.
      offer, peek, poll operations.
      Use case - task queues
      
   ArrayBlockingQueue: -
      Thread-safe.
      Backed by array. Thus bounded in size.
      Adding element to full queue results in blocking.
      Polling an empty queue results in blocking.
      Use case - Producer consumer problem.
      
   LinkedBlockingQueue: -
      Thread-safe.
      Backed by linked-list.
      Optionally bounded in size. Takes maxSize as constructor argument.
      
   ConcurrentLinkedQueue: -
      Thread-safe.
      Uses CAS (Compare-And-Swap) for more throughput. Also known as lock free.
      
   Deque classes: -
      ArrayDeque - Double ended queue. Backed by array. Can throw ConcurrentModificationException.
      LinkedList - Implements Deque interface.
      LinkedBlockingDeque
      ConcurrentLinkedDeque

   PriorityQueue
      Elements sorted based on their natural order (or Comparator provided in Constructor).
      Use case - task queues where tasks can have different priorities.
      
   PriorityBlockingQueue: -
      Thread-safe.
      
   DelayQueue: -
      Elements added, are available to be removed only after their delay-time is expired.
      
   SynchronousQueue: - 
      Holds single elements.
      Blocks for both producer and consumer to arrive.
      Use case - For safe/atomic transfer of objects between threads.
      
equals and hashCode: -
   equals required for all collections.
   equals and hashCode required for Maps and Sets (which are backed by Maps).

-------

Collections class:- 
   Utility methods: -
      sort(list, key) - guarantees stable sort
      reverse
      reverseOrder - returns Comparator for reversed order
      shuffle
      rotate(list, distance) - rotates elements by the distance specified
      binarySearch(list, key)
         list should be sorted else can get unpredictable results
         log(n) if list implements RandomAccess, else O(n)
         RandomAccess - Marker interface that says, collection supports fast random access, get(i). Typically backed by arrays.

   Methods returning wrapped instances: -
      empty - emptyList, emptySet, emptyMap etc.
      synchronized - synchronizedList, synchronizedSet, synchronizedMap etc.
      unmodifiable - unmodifiableList, unmodifiableSet, unmodifiableMap etc.
      singleton(t) - singleton (returns set), singletonList, singletonMap etc.

-------

Hierarchy and classes: -

![image](https://github.com/user-attachments/assets/2089708e-763a-4798-af06-811899207baa)

![image](https://github.com/user-attachments/assets/010b204a-ecb7-44cb-a0f7-854c11561495)

-------

**A.3  Java 8**
=

Topics in Java 8: -
   Streams
   Examples:
   Comparators
   Concurrency
   CompletableFuture
   StampedLock
   @Contended

**Streams: -**

   **Create:-**
   - Stream.of(T… a)
   - IntStream.rangeClosed(start, end) + LongStream & DoubleStream
   - Arrays.stream(array)
   - list.stream()
   - list.parallelStream()
   - Files.getLines()
   - Stream.generate(() -> Math.random());

   **Filter**: -
   - findAny
   - findFirst
   - filter
   - distinct
   - limit(long)
   - skip(long)

  **Operations**: -
  - sorted
  - boxed
  - min(comparator)
  - max(comparator)
  - count
  - forEach
  - flatMap // when each element maps to n elements
  - toArray
  - reduce (0, (c, e) -> c + e); // accumulator,
  - reducer function reduce (0, Integer::sum); Collectors
  
  **Collectors.toList()**: -
  - Collectors.toSet()
  - Collectors.toMap(keyMapper, valueMapper)
  - Collectors.toCollection(TreeSet::new)
  - Collectors.joining(", ") // needs stream of strings, use map(Object::toString) before this
  - Collectors.summingInt(Employee::getSalary)
  - Collectors.averagingInt(Employee::getSalary)
  - Collectors.summarizingInt(Employee::getSalary) [gives stats max,min,count,average]
  - Collectors.groupingBy(Employee::getDepartment)

**Examples**:

  **FlatMap**

        Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);
        Stream<String> words = lines.flatMap(line -> Stream.of(line.split(" +")));

  **Typical**

        books.stream()
             .filter(book -> book.year > 2005)  // filter out books published in or before 2005
             .map(Book::getAuthor)              // get the list of authors for the remaining books
             .filter(Objects::nonNull)          // remove null authors from the list
             .map(Author::getName)              // get the list of names for the remaining authors
             .forEach(System.out::println);     // print the value of each remaining element

  **Read Large File**: -
    Does not load whole file in memory. Though exceptions are wrapped in UncheckedIOException.

        try(Stream<String> stream : Files.lines(Paths.get(“absolute-path”))){
           stream.forEach(System.out::println);  
        }

  **Group By**: -

          Map<Person.Sex, List<Person>> byGender 
              = roster.stream().collect(Collectors.groupingBy(Person::getGender));
          
          ConcurrentMap<Person.Sex, List<Person>> byGender 
              = roster.parallelStream().collect(Collectors.groupingByConcurrent(Person::getGender))
          
          // Group employees by department
          Map<Department, List<Employee>> byDept
              = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment));
          
          // Compute sum of salaries by department
          Map<Department, Integer> totalByDept  
              = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment,Collectors.summingInt(Employee::getSalary)));
          
          // Partition students into passing and failing
          Map<Boolean, List<Student>> passingFailing 
              = students.stream().collect(Collectors.partitioningBy(s -> s.getGrade() >= PASS_THRESHOLD));


**Comparators**: - 
  vehicles.sort(Comparator.comparing(Vehicle::getWheels)); vehicles.sort(Comparator.comparing(Vehicle::getWheels)); vehicles.sort(Comparator.comparing(Vehicle::getWheels).thenComparing(Vehicle:getColor); //chaining

**Concurrency**: -

  **HashMap**: -
  - compute
  - computeIfPresent (blocking, so write smaller computations)
  - computeIfAbsent (blocking, so write smaller computations)
  - putIfAbsent
  - merge
  - getOrDefault (k, def)

  **Adders/Accumulators**: -
  - Much more performant than AtomicLong (have single copy across threads). Accumulators, each thread has own copy tracking its own counter, and when retrieval is triggered in any thread, all threads coordinate and perform total sum of all threads’ counts.
  - Basically no contention, during increment, decrement, add, thus much faster.
      - LongAdder
      - DoubleAdder
      - LongAccumulator
      - DoubleAccumulator
  - Operations
      - increment
      - decrement
      - add (long)
      - sum // retrieves result by coordinating between threads
      - reset

**CompletableFuture**: -
    Similar to JavaScript promises. Multiple async tasks can be chained (performed one after another on separate thread). Better alternative to future where the method get is blocking (waits indefinitely for the result). In completableFuture the current thread is not blocked. Each task is executed once previous task is completed.

  Also, the program itself becomes more readable.

       CompletableFuture.supplyAsync(() -> getStockInfo(“GOOGL”), executor)   // if executor is not passed it uses internal pool
              .whenComplete((info, exec) -> System.out.println(info))  // triggered once previous operation is finished
              .thenApply(Stock::getRate)   
              .thenAccept(rate -> System.out.println(rate))  
              .thenRun(() -> System.out.println(“done”)));  

So when you trigger this, it immediately returns the CompletableFuture instance, which can be used to check its status and such.
  - supply method takes Supplier which returns a value
  - thenApply method argument is Function which takes input and returns value
  - thenAccept method argument is Consumer which takes input
  - thenRun method argument is Runnable which only runs
  - CompletableFuture has no control of tasks while they are running in the executor. So cancel method just sets returned value as Exceptional.

**StampedLock**: -
  - Better alternative for ReadWriteLock.
  - It does optimistic reads so works faster only on less contended operations.
  - Not re-entrant
    
**@Contended**: -
  For fields shared within same cache line and if only 1 field in it is volatile, then for multiple threads, the cache line will be flushed and will be updated. Thus use of cache is of no use. To fix this, set the field to hot field by using @Contented so that JVM can pad the field so that it takes entire cache line and is not shared with other fields.


**A.4  Java Memory Model**
=

https://vimeo.com/181788144 ---> Recommended

What is it?
- Specification deciding how JVM can reorder instructions (for performance) aka ensures guaranteed ordering of of reads and writes under certain conditions (happens-before). Every JVM has to implement this spec.
- Barriers that forbid reordering instructions (load-load, load-store, store-load, store-store)
- Variables
   - volatile
   - final = all writes before volatile write will be reflected when/after volatile is read (potentially by other thread). Threads need to use the same volatile variable for this to work. For double/long (which occupy multiple word spaces, word-breakdown is forbidden to ensure integrity of data).
- Methods - synchronized
- Locks - normal objects used as locks, and lock classes like ReadWriteLock.
- Threads - When a new thread is started, it is guaranteed to see all values written before thread started.


**A.5  Java Concurrency**
=

**Resources**: - 
   - http://jcip.net/ -> Concurrency in Practice by Brian Goetz - Highly Recommended

**Table of Contents**: -
- I]**Basics**
   - Benefits of Threads
   - Thread safety
   - Race Condition
   - Solutions to compound operations
   - Synchronized
   - Liveness and Performance
- II] **Sharing Objects**
   - Data Visibility
   - Safe construction
   - Confinement
   - Immutability
   - Final Fields
   - Safe Publishing
- III] **Composing Objects**: -
   - State Ownership
   - Unmodifiable
   - Client-side locking
- IV] **Building Blocks**: -
   - Iterators and ConcurrentModificationException
   - Concurrent Collections
   - InterruptedException
   - Synchronizers
- V] **Task Execution**: -
   - Thread Pools
   - Uncaught exception handlers
   - Shutdown hooks
   - Daemon threads
   - Finalizers
- VI] **Applying Thread Pools**: -
   - Thread pool sizes
   - ThreadPoolExecutor
   - Threads
   - Task Queues
   - Saturation Policy
   - Thread Factory
- VII] **Avoiding Liveness Hazards**: -
   - Deadlocks
   - Starvation and LiveLock
- VIII] **Performance and Scalability**: -
   - Costs on performance
   - Steps
- IX] **Explicit Locks**: -
   - Lock and ReentrantLock
   - Advantages of lock classes
   - Read-Write lock
   - Custom Synchronizer

I] **Basics**: -

**Benefits of Threads**: -
- Exploiting multiple processors (Resource utilization) - Increasing core counts
- Simplicity of modeling applications - Distinct tasks can have own thread, and each can be written sequentially.
- Simplified handling of asynchronous events - If thread is blocked on IO, other threads can still run. Though these days OS allow 100s of thousands of threads, so blocking is not a major issuer anymore. Thus NIO is not as crucial anymore (because its very complicated to implement).

Even if your class doesn't use threads, these do
- Underlying frameworks
- RMI
- JVM (for GC + Main)
- Timer
- Servlets & JSP

**Thread safety**: -
Correctness means that a class conforms to its specification. A good specification defines invariants constraining an object's state and postconditions describing the effects of its operations.
No set of operations performed sequentially or concurrently on instances of a thread-safe class can cause an instance to be in an invalid state.

**Race Condition**: -
- A race condition occurs when the correctness of a computation depends on the relative timing or interleaving of multiple threads by the runtime
- Occurs usually with check-then-act (check stale value). Eg: Lazy initialization.
- Data races is different than race condition. Data races is when thread access (read/write) data to variable without any synchronization.

**Solutions to compound operations**: -
- Atomic classes (if only single variable is the issue)
- Synchronized (if multiple variables are to be updated atomically)

**Synchronized** : -
- Aka Intrinsic locks, Mutexes, monitors
- Are re-entrant
- Re-entrancy can help for overridden synchronized methods. Call to super.method() tries to re-acquire lock, and is permitted.
Allowing Object class to act as a lock (instead of special classes) was a mistake in JVM design. JVM implementors now have to make trade offs between object size and locking performance.

**Liveness and Performance**: -
- If scope of synchronized block is too large (say entire method of service). The whole performance benefit of multi-threading might be wiped off if service is accessed by lot of threads.
- Scope of synchronized block should be small enough that it covers all mutable state.

II] **Sharing Objects** : -

**Data Visibility** : -
- When data is not synchronized, other thread might read stale data (due to caching of variables in CPU registers and L1, L2)
- 64-bit operations can be treated as 2 32-bit operations, thus need to be synchronized
- Synchronized keyword ensures other thread reads latest data
- Volatile keyword does the same
- Semantics of volatile does not guarantee atomic increment!! Thus use volatile generally as status flags and such.
Debugging tip: --server argument can hoist variables out of if condition (due to heavier optimization), while client JVM may not. Thus don't just think, if it works in client it works on server.

**Safe construction**: -
- Do not let this reference escape the constructor. Even if it is last statement of constructor, the escaped reference of this, may not be pointing to completed object.
- Thus instead of constructors, listeners use setListener factory methods.

**Confinement**: -
- Confining variables within method is best. For primitives its easy, but for objects, have to ensure its reference is not escaping. Thus Java has Collections.unmodifiableCollection and such.
- Note: unmodifiableCollection doesn't allow references to be updated, but objects' can still be updated if they are mutable. Eg: Map<String, Vehicle>.. Vehicle object can still be updated.
- ThreadLocal can also be used if its instance variable, but has to be thread-confined.

**Immutability** : -
- Simple. Objects that cannot be modified, there are no threading issues.

**Final Fields**: -
- Guarantee initialization safety.

      public Holder holder;
      public void initialize() {
           holder = new Holder(42);   // not guarantee to be done atomically, unless variable is final
      }

**Safe Publishing** : -
- Placing a key or value in a Hashtable, synchronizedMap, or Concurrent- Map safely publishes it to any thread that retrieves it from the Map (whether directly or via an iterator);
- Placing an element in a Vector, CopyOnWriteArrayList, CopyOnWriteArraySet, synchronizedList, or synchronizedSet safely publishes it to any thread that retrieves it from the collection;
- Placing an element on a BlockingQueue or a ConcurrentLinkedQueue safely publishes it to any thread that retrieves it from the queue.

Using a static initializer is often the easiest and safest way to publish objects that can be statically constructed: public static Holder holder = new Holder(42);

III] **Composing Objects**: -
- Making a class thread-safe means ensuring that its invariants hold under concurrent access; this requires reasoning about its state. Objects and variables have a state space: the range of possible states they can take on. The smaller this state space, the easier it is to reason about.
- By using final fields wherever practical, you make it simpler to analyze the possible states an object can be in. (In the extreme case, immutable objects can only be in a single state.)


**State Ownership**: - 
 - Owner of the state can help reason about the mutable access. In Java, there can be shared ownership or transferred ownership (since objects are passed as reference).

**Unmodifiable**: -
- unmodifiableCollection doesn't allow references to be updated, but values themselves can still be updated if they are mutable. Eg: Map<String, Vehicle>.. Vehicle object can still be updated.
- locations = new ConcurrentHashMap<String, Point>(points);
- unmodifiableMap = Collections.unmodifiableMap(locations);
- unmodifiableMap stores "live" view of the locations map (i.e. any update to locations are reflected in unmodifiableMap).
- To just give static view (non-live view) wrap it in new HashMap instance and then in unmodifiable.
- Collections.unmodifiableMap(new HashMap<String, Point>(locations));}

**Client-side locking**: -
- It can be difficult, because client has to lock on same object which the ThreadSafeClass uses. For example creating a putIfAbsent method in List class
   - Cannot be achieved through having separate lock.
   - Can be achieved using same lock as the list. But here locks are spreads over 2 classes (this and List)
   - Best is to use composition, where you extend list, and ask clients to use this class.

IV] **Building Blocks**: -

   **Iterators and ConcurrentModificationException**: -
      - Iterators are implemented by associating a modification count with the collection: if the modification count changes during iteration, hasNext or next throws ConcurrentModificationException. However, this check is done without synchronization, so there is a risk of seeing a stale value of the modification count and therefore that the iterator does not realize a modification has been made. This was a deliberate design tradeoff to reduce the performance impact of the concurrent modification detection code.
      - Easiest way to avoid this, is to hold lock on the list/collection during iteration (in client code). This will come at a cost of scalability. This again, can be avoided, by making copy of entire collection (by wrapping in a lock), and then without locking iterating over that cloned collection. But this comes with increased cost of memory & speed (copying collection every time).
      - There are also hidden iterators, like when you do toString on collection it internally iterates over all elements appending to StringBuilder. In such cases, on client side use the Collection.synchronizedSet() and then perform all operations.

   **Concurrent Collections**: -
   
   **Java 5**: -
   - ConcurrentHashMap instead of synchronizedMap (with operations put-if-absent, conditional remove, replace)
   - CopyOnWriteArrayList instead of synchronizedList
   - CopyOnWriteArraySet instead of synchronizedSet
   - Queue & BlockingQueue along with ConcurrentLinkedQueue
   - PriorityQueue (non concurrent)

   **Java 6** : -
   - ConcurrentSkipListMap instead of synchronized TreeMap
   - ConcurrentSkipListSet instead of synchronized TreeSet
         
   **ConcurrentHashMap** : -
   - Lock striping
   - Concurrent readers and writers, thus high throughput
   - Weakly consistent iterators instead of fail-fast
   - Weakly consistent size and isEmpty too
   - Cannot lock at client side using map object, because internally it uses different locks
   - Due to above reason, we cannot exclusively lock map and thus cannot write our own put-if-absent
   - Thus this map provides such operations put-if-absent, conditional remove, replace

   **CopyOnWriteArrayList & CopyOnWriteArraySet**: -
   - Do not throw ConcurrentModificationException.
   - Copy underlying data during, modification
   - No locking needed during iteration

   **Other classes**: -
   - ArrayBlockingQueue & LinkedBlockingQueue.
   - SynchronousQueue
   - ArrayDeque & LinkedBlockingDeque (work stealing: each consumer has its own deque and it steals from other consumer's deque's tail if its own is empty)

   **InterruptedException**: -
   - Threads waiting in blocked state can be interrupted, using interrupt method. It has boolean flag for setting this.
   - Good for cancelling long waiting tasks.
   - If your code calls method which throws InterruptedException then:
   - Choice 1 - Throw InterruptedException when called
   - Choice 2 - Catch it, and restore the interrupted flag

   **Synchronizers**: -
   - **Latches**: -
      - Can act as a gate, where all threads stop at the gate, and allowed once gate is opened.
      - This binary closed-open action is good for implementing 'Resource is initialized, now dependent actions can begin'
      - Eg: Wait for all dependent services to init, wait for all players to arrive, wait until all worker threads finish etc.
      - CountDownLatch: await method, thread waits till count decrements to zero, or is interrupted or wait times out
      - FutureTask can also act as latch. future.get() method waits until task is completed and returns results.
   - **Semaphores**: -
     - Permits (ac
     - quire and release)
     - Useful in creating bounded collections
     - Can instead use BlockingQueue, if resources themselves are to be tracked. Eg: Object pool
   - **Barriers**: -
      - Similar to Latches with key difference.
      - Latches are waiting for events, barriers are waiting for other threads
      - CyclicBarrier waits for fixed number of threads arrive at a point, repeatedly.
      - If all threads reach barrier point, barrier is passed and it resets.
      - await call returns arrival index to each thread, which can be used to "elect"
      - CyclicBarrier constructor accepts count (of threads) and Runnable (to run when threads reach this point).
      - Excellent for assigning sub-problems to threads and merging all results when barrier is reached.
   - **Exchanger**: -
      - Similar to Barrier that both wait until other arrives at same point.
      - Once reached they can exchange an object.
      - This is transfer of ownership of object (safe publication)
      - Example: Consumer exchanging an empty buffer with the producer, for a full buffer.
        
V] **Task Execution**: -

  - **Thread Pools**:
       - Threadpool with its bounded pool helps throttle the inputs/requests so as to not exhaust available resources.
       - Single Threaded Executors provide synchronization guarantee that writes made by a task will be visible to subsequent tasks.
      **Types**
      - newFixedSizeThreadPool - creates new thread, if one dies due to exception
      - newCachedThreadPool - keeps increasing threads
      - newSingleThreadExecutor - consumes task based on queue type (FIFO, LIFO, Priority etc), resurrects thread if dead
      - newScheduledThreadPool - supports delayed and periodic execution similar to Timer
      - Usually shutdown call is immediately followed by awaitTermination.

 - **Uncaught exception handlers**: -
      - Thread provides facility for UncaughtExceptionHandler. When thread dies due to some exception, JVM checks if it has exception handler, if not it checks its ThreadGroup, if not then its super ThreadGroup and so on. Final system level ThreadGroup just prints stack trace to System.err
  
 - **Shutdown hooks**: -
      - JVM also provides Runtime.addShutdownHook for when it attempts to shut down.
      - Then it runs finalizers if runFinalizerOnExit is true
      - It does not attempt to shutdown application threads, they die abruptly
      - If these hooks do not complete, they hang the JVM. Thus do proper synchronization and not dead lock them.

 - **Daemon threads**: -
      - Daemon threads do not stop JVM to shutdown
      - GC, housekeeping threads are daemon threads
      - Threads inherit daemon status of the owner thread
      - When JVM halts, they do not call finally for daemon, nor cleanup their stacks, nor call finalizers.
      - Thus better to use them sparingly

 - **Finalizers**: -
      - Opportunity given by JVM to reclaim/free any resources being held up
      - Not guaranteed to run
      - Avoid as much as possible, catch-finally can more often do better job

VI] **Applying Thread Pools**: -

   - **Thread pool sizes**: -
      - If its too big, it can exhaust memory (& lot of overhead of creating/managing them)
      - If its too small, not completely utilizing the CPU
      - Usually, N (number of CPU cores) is right size of pools
      - Though, if tasks do IO then, not all threads will be schedulable (tasks will be waiting for some resource), so its okay to increase size of threadpool.
      - int N_CPUS = Runtime.getRuntime().availableProcessors();
      - Other deciding factors: memory, file handles, socket handles, and database connections
   
   - **ThreadPoolExecutor**: -
     - **Threads**: -
        - newFixedThreadPool: corePoolSize == maximumPoolSize
        - newCachedThreadPool: corePoolSize = 0 and maximumPoolSize = Integer.MAX_VALUE
        - Keep alive: how long to wait before unused thread is reclaimed (trade off)
     - **Task Queues**: -
        - newFixedThreadPool and newSingleThreadedExecutor use unbounded LinkedBlockingQueue
        - Try to use bounded (LinkedBlockingQueue, ArrayBlockingQueue or PriorityQueue)
     - **Saturation Policy**: -
        - When bounded queue is full, what to do when task is submitted
        - Abort - throw RejectedExecutionException
        - Discard - discard silently
        - Discard oldest - discards oldest from queue
        - Caller Runs - return task to caller, so that caller thread can run it instead

VII] **Thread Factory** : -
        - Used to create new threads
        - By default new non-daemon threads are created
        - Can be overridden to create special threads which do say logging

VII] **Avoiding Liveness Hazards**: -
   - **Deadlocks**: -
        - Database systems are great at handling deadlocks; they back-off certain transactions such that locks are released.
        - JVM is not so kind. When threads are deadlocked, that's it, game over.
     Transfer money is classic examples (with synchronized block on from and to accounts). If 2 calls are made, where 1st case arguments are from then to, and in 2nd case, its to then from. They may deadlock. To solve, either get comparable int keys or System.identityHashcode(), and order which account to be synchronized first. So no matter what's order of arguments, you always lock same account first, avoiding deadlock.
           - **How to avoid**
               - Use only 1 locks (so no ordering issues)
               - Order locks if multiple (ensure ordering is same no matter order of arguments)
               - Use tryLock method of lock classes
        
   - **Starvation and LiveLock** : -
      - Starvation is when thread is stuck
      - Livelock is when thread keeps running (eg: message listener throws exception, rolls back then again tries processing of same object)

VIII] **Performance and Scalability**: -
   - Amdhal's law - How much a program can be theoretically sped up.
   - Trick is to divide into multiple tasks with their own data structures, and converge the results (this last step will be only step that will be sequential)
   - ConcurrentLinkedQueue is twice as fast as synchronizedLinkedList, because former has only final pointer updates as sequential while latter synchronizes on whole list.

     - **Costs on performance**: -
        - Context switching
        - Synchronization
        - Data locality (memory cache)
        - Blocking on locks
    - **Steps**: -
        - Reduce lock contention
        - Reduce scope - get in / get out
        - Reduce lock granularity (too many times in and out is not good too)
        - Lock striping
        - Avoid hot fields
        - Non blocking (compare-and-swap)
        - Say no to object pooling - Java is super fast at new allocation, but getting objects from pool requires synchronization

IX] **Explicit Locks**: -
   - **Lock and ReentrantLock**: -
      - Lock implementations must provide the same memory-visibility semantics as intrinsic locks, but can differ in their locking semantics, scheduling algorithms, ordering guarantees, and performance characteristics.

   **Advantages of lock classes**: -
   - More flexible. Need not release lock in same block of code unlike synchronized
   - "Synchronized block" threads cannot be interrupted while they are waiting for lock.
   - lockInterruptibly can stop waiting for lock on interrupt
   - Deadlock can be avoided by trying to acquire lock, and releasing already acquired, if cannot acquire new one.
   - Intrinsic locks can't release locks on timeout
   - Since Java 6, performance of intrinsic and reentrant lock is very similar. Earlier it used to be slower.

- Always do lock.unlock in finally block
- Locks can be fair/unfair. Fair locks implement queues to handle requests for a lock. Ofcourse, fairness comes with cost of performance.

   **Read-Write lock**: -
   - Allows multiple concurrent readers, but only single writer
   - Great for data structure with lot of reads
   - More complex to implement thus slightly slower than reentrant lock

   **Implementation factors**: -
   - Release preference - If writer is running, and readers+writers are waiting, preference to writer?
   - Reader barging - If reader is running, and readers+writers are waiting, preference to reader? Good for throughput but writer can become starved
   - Reentrancy - Are they reentrant
   - Downgrading - what if writer lock owners wants only reader lock
   - Upgrading - What if reader lock owner also wants writer lock

   **Custom Synchronizer**: -
   - Conditional queues - Threads waiting for an object lock which reflects certain condition.
   - Explicit class called Condition for implementing conditional queues (which can be implemented using intrinsic locks too).
   - Condition is associated with single lock. Lock.newCondition()
   **Advantages**: -
   - Fairness in wait
   - Timeout facility (flexibility)

**6] Garbage Collection**: -














**Concepts**: -
- Throughput - Percentage of time application runs vs GC
- Latency - Amount of pause time for application waiting for GC to complete
- Memory - Amount of memory used to store the objects aka heap (along with GC related data structures)

**Trade offs**: -
- If memory is less, throughput is less, because JVM has to constantly do GC
- If memory is more, latency is high, because JVM has to sweep huge space to do GC

**Use cases**: -
- For trading applications, deterministic (more average) latency is better than sudden spikes (increased worst case latency).
- For batch applications, it might be ok for increase worst case latency, if it helps gain more throughput

**Tweaks**: -
- To large extent, more memory for GC helps in increasing throughput
- Worst case latency can be reduced by keeping heap size small (& live set small)
- Frequency of GC can be reduced by managing heap & generation sizes
- Frequency of large pauses can be reduced by running GC with application, sometimes at cost of throughput (because it runs longer due to 2 STW pauses, and one thread is used by GC which could've been used by application).

**Object lifetimes**: -
- Infant mortality / Weak generational hypothesis - Most objects die young.
- Thus, generational GC algorithms provide magnitude-of-order better throughput.
- How? Region with newly allocated object is sparse for live objects, they can be quickly copied over and region can be wiped entirely.
- If application keeps allocating objects that live too long. The generational split becomes useless, and GC takes long time. Because old generation is too big (& not so sparse).
- Lifetime of object is recorded by JVM as number of GC cycles survived.

**Stop the World Events**: -
- Collectors need application execution to stop for practical reasons.
- Threads are signalled to stop. Threads stop when they reach safe points of execution.
- If thread is busy (copying large array, cloning a large object), it might be few milliseconds till this point.
- ‑XX:+PrintGCApplicationStoppedTime this flag is used to print the time taken to reach safe point.
- Once GC STW event is over, all threads are free to resume. An application with large number of threads can suffer scheduling pressure. It might be more efficient to use different collector then.

**Heap organization in HotSpot**: -
- Heap is split in Young and Old (Tenured) generation
- Young generation is split in - Eden and Survivor (1,2) spaces
- PermGen is used to store effectively immortal objects (Classes, static strings etc).
- In Java 7, interned Strings were removed from PermGen.
- In Java 8, PermGen space itself is replaced by MetaSpace.

**Object allocation**: -
- Each thread is assigned TLAB (Thread Local Allocation Buffer) to allocate new objects.
- Since there is no conflict between threads, object allocation is just a bump-the-pointer, and is faster than MALLOC in C. Roughly 10 machine instructions.
- When TLAB is exhausted new TLAB is requested from Eden. If Eden is filled, Minor GC is triggered.
- If a large object (size greater than TLAB) is to be allocated, it is done directly in Eden or Old Generation.
- -XX:PretenureSizeThreshold=<n> If this is smaller than TLAB, then small objects are still allocated in TLAB itself, not in Old generation.

**Minor Collection**: -
- Called when Eden is full.
- Live objects are moved to one of the survivor spaces.
- If survivor space is full or object has live too long (XX:MaxTenuringThreshold=<n>) it is moved to tenured generation.
- Major cost of minor collection is in copying live objects to survivor / old generation. Thus, overall cost depends on number of objects to be copied not the size of Eden.
- Thus, if new generation size is doubled, total minor GC time is almost halved (thus increasing the throughput). Assuming number of live objects remain constant.
- Minor collections are STW events. This is becoming an issue as heaps are getting larger, with more and more live objects.
- This algorithm is called mark-and-copy

**Marking Live Objects**: -
- All objects in graph starting from GC Roots
- GC roots = references from application, JVM internal static fields and thread stack-frames
- References from old generation to young generation (aka cross generational references) are also tracked
- Card tables are used for this. Card tables are array of bytes. Each byte represents 512 bytes of old gen. If byte is set, it means corresponding 512 bytes of old gen has reference to young gen objects.
- During minor collection, all such cards are checked, then all those 512 byte regions are checked for references. Thus, minor collection latency also depends on number of old gen to young gen references.

**Major Collection**: -
- JVM tries to predict, set % threshold and start collection when threshold is passed.
- When object cannot be promoted from younger gen due to lack of space in old gen, then FullGC is triggered.
- FullGC = minor collection + major collection (including compaction).
- FullGC is also triggered when old gen size needs to be changed. This can be avoided by keeping Xms same as Xmx
- Compaction is likely to cause largest STW

**Serial Collector**
- Smallest footprint of any collectors
- Uses single thread for both minor and major collections.
- Objects in old gen are allocated with simple bump the pointer technique
- Major GC is triggered when old gen is full

**Parallel Collector**
- Parallel Collector - Multiple threads for minor GC, and single thread for major GC.
- Parallel Old Collector - Multiple threads for both minor and major GC. Default since Java 7u4
- Doesn't run with the application.
- Greatest throughput in multi-processor systems. Great for batch applications.
- Cost of Old GC, is proportional to number of objects, thus doubling old gen size can help in increasing throughput (larger but fewer GC pauses).
- Minor GCs are fast because promotion in old gen is just bump-the-pointer.
- Major GC takes 1-5 seconds per live data
- Allows for -XX:+UseNUMA to allocate Eden space per CPU socket (can increase performance)

**Concurrent Mark and Sweep**
- Parallel (multiple threads) for Minor GC
- Runs with application to try to avoid promotion failure. Promotion failure causes FullGC.
- CMS =
   - Initial mark = Find GC roots
   - Concurrent mark = mark all objects from GC root
   - Concurrent pre-clean = check for updated object references & promoted objects during mark (Card Marking technique)
   - Re-mark = mark all objects in pre-clean
   - Concurrent sweep = reclaim memory and update free-lists
   - Concurrent reset = reset data structures used
- During concurrent sweep, promotions can occur, but those are in free-lists which are not being swept anyways. So there is not conflict.
- Minor GCs can keep happening while Major GC is happening! Thus the pre-clean phase.
- Slower minor GCs during promotion. Cost of promotion is higher since, free-lists have to be checked for suitable sized hole.
- CMS is not compacting collector, so when object promotion fails due to not having enough space in free-lists. CMS is followed by compaction. This latency can be worse than Parallel Old collector.
- Decreases latency, but less throughput. Avg ¼ GC threads per 1 CPU core.
- Throughput reduction between 20-40% compared to parallel collector (& based on object allocation rate).
- 20% more space required.
- "Concurrent mode failure" - When CMS cannot keep up with high promotion rates. Increasing heap makes it even worse, because sweeping will take even more time.

**G1 Garbage First** 
- Soft real-time targets. Spend x milliseconds in GC out of y milliseconds.
- Divides heap into large regions ~2048
- Categorizes regions in Eden, Survivor and Old gen spaces.
- Minor GC is triggered, when all Eden regions are filled.
- Selects closest set of nearly free regions (called Collection Set), to move live objects, essentially causing regions to be empty. Thus it approaches problem incrementally, as opposed to CMS which has to perform on entire Old gen.
- Objects larger than 50% of region are saved in "humungous region"
- Regions can have objects being referenced from multiple other regions. These are tracked using Reference Sets. Thus, while moving live objects, all the references to such objects need to be updated. Thus, even minor collections can potentially be longer than Parallel or CMS collector.
- It avoids collecting (moving) from regions which have high references. Unless it has no other option.
- G1 is target driven on latency –XX:MaxGCPauseMillis=<n>, default value = 200ms
- G1 reduces worst case latency, at the cost of higher average latency.
- Compactions are piggybacked on Young Gen GC.
- During reference changes, cards (arrays pointing to 512 bytes of a region) are marked dirty, and source-target details are placed in dirty card queue. Depending on number of elements in queue (white, green, yellow, red) G1 starts threads which take information from queue and write to Remembered Set. More the queue is full, more G1 threads try to drain it. Remembered set will be heavily contended if all threads directly write to RS, its better if only specific G1 threads (1 or 2) write to them.
- If Young GC cannot finish within maxTargetPauseTime, then # of Eden regions are reduced to finish within the target.
- Old GC is triggered when total 45% is full, and is checked just after Young GC or after allocating humongous object.
- VID - https://www.youtube.com/watch?v=Gee7QfoY8ys

**Shenandoah**
- 10% hit in throughput, but 7x more responsive.
- Avg Pause 5x better - 60ms (vs 600ms in G1).
- Max Pause 3x better - 500ms (vs 1700ms). Lot of headroom.
- Target - 10ms average, and max 100ms.
- Region based like G1 (remembered sets, collection sets)
- Concurrent mark and sweep same as CMS and G1
- It does compaction though + concurrently while threads are running
- Trick is to create indirection pointer to objects, and after copying live objects, atomically update the indirection pointer to copied version of the objects.
- Not Generational. Claim is, Weak Generational Hypothesis is no longer applicable.

**Other pauses not related to GC**
- Networking, Disk read/writes, Waiting for DBs
- OS interrupts (~5ms), this doesn't show up in logs
- Lock contention


**7]JVM Internals**
- Some of the topics listed below are tricks JVM uses to improve performance. Subset of these can be exploited to further improve application performance.

Note: These topics are highly unlikely to come up in an interview. Feel free to just glance through without digging deep.

**Compressed pointers**
- 32 bit references can address 4GB, while 64 bit can reference 2^64 bytes (though limited by OS/RAM on the machine).
- Having 64 bit reference for every object increases memory usage. JVMs use compressed pointers to address this issue.
- Basic idea is to store 32-bits per reference and then add to a base address to find final 64-bit address.
- Flag: -XX:+UseCompressedOops. Latest versions of 64-bit Java have this argument by default.
- Addresses upto 4gb untranslated.
- Addresses 4gb to 28gb, remove 3bits, because Java has 8 byte word aligned, thus 3 bits need not be stored.
- Important because Java has more references. In C++ memory layout follows struct layout.
- Java 8 has JVM args, +XX:ObjectAlignmentInBytes=16 for heap between 32gb and 64gb.

**String interning**
- Interning = storing strings in a pool and re-using them
- If you intern a set of all strings, you can compare them by == improving performance.
- It is stored internally as a hashmap (it is native C code, not Java code).
- More details here and here
- How would you implement your own string interning?


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

**Thread Affinity**: -
- Makes the thread stick to a CPU core, even if it has no tasks left to perform. Unlike normal threads, this won't go into sleep/wait.
- Thread performs busy-spin. Note: This is wasting of CPU resources, and it can lead to thread starvation since other threads do not get access to that core.
- Thus, it needs to be used for the right applications. Helpful in latency critical applications like FX Trading.
- Thread affinity only works for Linux and there are Java libraries available (https://github.com/OpenHFT/Java-Thread-Affinity) to use the same

**Other topics**: -
- How does default hashCode method work? (https://srvaroa.github.io/jvm/java/openjdk/biased-locking/2017/01/30/hashCode.html)
- What is Biased locking (https://blogs.oracle.com/dave/entry/biased_locking_in_hotspot)
- JVM Threads link with OS threads (http://openjdk.java.net/groups/hotspot/docs/RuntimeOverview.html#Thread%20Management%7Coutline)
- Class Loaders (https://zeroturnaround.com/rebellabs/rebel-labs-tutorial-do-you-really-get-classloaders/)
- Memory consumptions of primitives and boxed variables (http://java-performance.info/overview-of-memory-saving-techniques-java/)
- Hoisting variables: JVM can hoist variables out of for loops to improve performance. example(http://stackoverflow.com/a/9338302/3494368)
- Escape analysis: JVM can choose to place a method local object (if it never escapes the method) in Thread-stack instead of heap. Improves performance since that object doesn't go through GC (can be just deleted once method completes).



--------------------------------

**Table of Contents**

**- 1] Creating and Destroying Objects**
- Consider static builders
- Service Interface Pattern
- Builder pattern
- Singleton with private instance or enum
- Private Constructor
- Avoid creating unnecessary objects
- Clear memory references
- Avoid finalizers
**- 2] Methods common to all objects**
- equals
- hashcode
- toString
- clone
- comparable
**- 3] Classes and Interfaces**
- Accessibility
- Private fields with accessor methods
- Make fields as much immutable as possible
- Composition over inheritance
- Override in inheritance
- Prefer interfaces to abstract classes
- Class hierarchies over tagged classes
- Function objects to represent strategies
- Favor static member class over non-static
**- 4] Generics**
- Don't use Raw types
- Prefer Lists to arrays
**- 5] Enums and Annotations**
- Enums instead of int constants
- Prefer annotations over naming patterns
**- 6] Methods**
- Definition
- Var args
- Return empty collections instead of null
**- 7] General**
- Use Serializable Judiciously


**1] Creating and Destroying Objects**
**Consider static builders**: -
- They can have any name, thus can have multiple methods with same parameters (unlike constructors)
- They can return cached objects (eg: Boolean.valueOf)
- They can return their subtype, even class objects which are not public. Eg: Collection has 32 factory methods, return type of many are non-public classes. Ofcourse, the interface they extend is public. Returning such interface backed classes, also help in returning specific type based on argument. Eg: EnumSet returns RegularEnumSet or JumboEnumSet based on the argument. In future, JDK can add more types, without client/caller knowing about them. See service interface pattern below
- They can reduce verbosity of parameterized types. Eg: Maps.newHashMap()

**Service Interface Pattern**: -
- Here same pattern as above, where the implementation classes are not even known upfront.
- Example: JDBC connection driver classes. DriverManager.registerDriver, and DriverManager.getConnection.
- It needs to provide, registration API and then get service API
- Cannot subclass and take advantage of constructors. Though this enforces Composition instead of inheritance, so its not so bad.
- Cannot easily distinguish between constructing methods, and other methods. Need to use some convention to make it easy. Eg: newInstance, valueOf, of etc.

**Builder pattern**: -
- When too many parameters use builders instead.
- In Builders, each parameter setting can be through a good name method. In constructor its difficult to remember.
- Can easily add optional parameter support.

**Singleton with private instance or enum**: -
- static final variable (also provides init guarantee)
- Lazy loading (double checked)
- Enums (by default lazy, and provides init guarantee)

**Private Constructor**: -
**Avoid creating unnecessary objects**: -
- Eg: Sring abc = new String("some value"); instead use String abc = "some value"
- Choose primitives over boxed, check for unnecessary boxing and unboxing

**Clear memory references**: -
- Let objects go out of scope quickly
- If not, nullify reference (eg: Stack.pop, within method, elements[size] = null)
- Check caches and message listeners, they hold references

**Avoid finalizers**: -
- JVM does not guarantee they will be called
- If called, they can be called anytime, not immediately after object is eligible for GC
- Never release resource in finalizer, if it does not run, the resource will still be lock (or in inconsistent state)
- Hampers performance
- Instead use explicit close methods like OutputStream, java.sql.Connection etc
- These classes also use finalizers, but that is safety net

**Methods common to all objects**: -
**equals**: -
- If super class has implemented equals, then its okay to not implement (eg: Set used AbstractSet)
- Reflexive (equal to self), transitive, symmetric, consistent (unless modified)
- Maintain Liskov Substitution Principle, within equals (don't check o.getClass() == this.getClass() instead check with instanceof
- Consistent - Don't use external resources (eg: IP address)

**hashcode**: -
- every class which overrides equals must have it
- Consistent across multiple calls
- Dont use any fields, which are not used for equals
- You can exclude redundant fields (ones which are always same for all objects)
- You can cache the hashcode and return that (like String class), but then need to track if value is modified.

**toString**: -
**clone**: -
- Cloneable interface. Its a mixin interface. Does not have clone method.
- Object class's clone method is protected
- Atypical - Presence of Colenable modifies behavior of Object.clone() behavior. If present it returns object which is field by field copy, and if not present, then .clone method throws CloneNotSupportedException
- Cloning is not done using constructor
- If you override clone do return super.clone(), if all classes do that up the chain, then Object.clone will be called and you will get the right copy
- This is important because spec doesnt enforce anything from Cloneable interface. So someone might override clone and not clone, nor call super.clone, causing problems
- Note: Objects.clone() creates a shallow copy
- If you override and write clone, ofcourse you cannot set final field, thus need to remove final modifiers
- Object's clone method is declared to throw CloneNotSupportedException, but overriding clone methods can omit this declaration.
- Like constructor, clone method should not call non-final methods, because super object might not be properly constructed yet, causing some data corruption
- Clone method must be synchronized in case of concurrency
- In short, you are better off, creating and using a copy-constructor

**comparable**:- 
- Opposing sign for symmetry x.compareTo(y) == -y.compareTo(x)
- If compareTo returns 0, objects should ideally be equal (but thats not in contract)
- Weird: Inserting new BigDecimal("1.0") and new BigDecimal("1.00") in HashSet stores 2 elements (they use equals), while TreeSet stores only 1 element (they use compareTo)

**Classes and Interfaces** : -
**Accessibility**: - 
- Make fields, classes etc as much inaccessible as possible (private, protected, package, then public)
- Anything that is public is now part of API, thus difficult to make private later
