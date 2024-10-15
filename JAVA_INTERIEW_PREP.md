
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
- **Basics**
   - Benefits of Threads
   - Thread safety
   - Race Condition
   - Solutions to compound operations
   - Synchronized
   - Liveness and Performance
- **Sharing Objects**
   - Data Visibility
   - Safe construction
   - Confinement
   - Immutability
   - Final Fields
   - Safe Publishing
- **Composing Objects**: -
   - State Ownership
   - Unmodifiable
   - Client-side locking
- **Building Blocks**: -
   - Iterators and ConcurrentModificationException
   - Concurrent Collections
   - InterruptedException
   - Synchronizers
- **Task Execution**: -
   - Thread Pools
   - Uncaught exception handlers
   - Shutdown hooks
   - Daemon threads
   - Finalizers
- **Applying Thread Pools**: -
   - Thread pool sizes
   - ThreadPoolExecutor
   - Threads
   - Task Queues
   - Saturation Policy
   - Thread Factory
- **Avoiding Liveness Hazards**: -
   - Deadlocks
   - Starvation and LiveLock
- **Performance and Scalability**: -
   - Costs on performance
   - Steps
- **Explicit Locks**: -
   - Lock and ReentrantLock
   - Advantages of lock classes
   - Read-Write lock
   - Custom Synchronizer

**Basics**: -

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

