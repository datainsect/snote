0. what is the difference between repartition and coalsce?
   0>. repartition(partitionsNum) = coalsce(partitionsNum,shuffle=true);
   1>. repartition 是经过shuffle的再分区；在将partition变大或者变小很多(1000->1)的情况下使用。
   2>. coalsce(partitionsNum,shuffle=true): partition变动不大(1000->100)，是一个narrowDependency


1. what is the difference between groupbykey,reducebykey,aggregateByKey?
   0> 都是调用combineByKeyWithClassTag，最大的区别在于groupBykey 将mapsideCombine设置为false

2. Parent/getDependenct


3.what is map/combiner/reduce/partition function type for hadoop?
  IMapper extends Mapper[Tkey,Tvalue,Tkey1,Tvalue1] :
      map(key:Tkey,value:Tvalue,context:Context):void = {
         key1:Tkey1 = getMapKey(key,Value);
         value1:Tvalue1 = getMapValue(key,value);
         context.write(key1,value1);
      }
   
  IPartioner extends Partitioner[Tke1,TValue1] :
      getPartition(key1:Tkey1,value1:Tvalue1，numPartitions:Int):void = {
         num:Int = getParition(key1,value1,numPartitions);
         reutrn num;
      }
      
  ICombiner  extends Reducer[Tkey1,Tvalue1,Tkey1,Tvalue1]:
      reduce(key:Tkey1,values:Iterable[Tvalue1],contex:Contex):void = {
         key1:Tkey1 = getCombineKey(key,Values);
         value1:Tvalue1 = getCombineValue(key,values);
         context.write(key1,value1);
      }
      
    
   IReducer  extends Reducer[Tkey1,Tvalue1,Tkey2,Tvalue2]:
      reduce(key:Tkey1,values:Iterable[Tvalue1],contex:Contex):void = {
         key1:Tkey1 = getCombineKey(key,Values);
         value1:Tvalue1 = getCombineValue(key,values);
         context.write(key1,value1);
      }
      
4.what is all the type for all operators(transformation(narrow/shuffle)/action) in spark:
    0> Narrow Transformations:
       map:
       flatMap:
       mapPartitions:
       filter:
       union:
       keyBy:
       zip:?
       zipWithIndex:?
       sample:
       
    1> shuffle Transformations:
       distinct:
       groupBy:
       groupByKey:
       reduceBy:
       reduceByKey:
       combineByKey:
       cogroup:
       join:
       sortByKey:
     
     2> actions :
        collect:
        count:
        countByKey:
        first:
        take:
        foreach:
        saveAsTextFile:
        saveAsObjectFile:
        reduce:
        
5.how to write a partitioner in spark:
 def IPartition(partitionsNum: Int) extends Partitioner {
      
      require(partitions >= 0, s"Number of partitions ($partitions) cannot be negative.")
      override def numPartitions: Int = partitions
      
      override def getPartition(key: Any): Int = {
         val k = key.asInstanceOf[IClass]
         nonNegativeMod(k.hashCode(),numPartitions)
      }
 }
 
6.saveastextFile saveAsObjectFile 和 serillizer 的关系

7.combineByKeyWithClassTag 求平均值怎么求

8. secondarySort in spark:

9. wordCount in spark:
   val input = sc.textFile("xxxx");
   val words = input.flatMap(line => line.split(" "));
   val wordsNum = words.map(word => (word,1));
   val result = wordsNum.reduceByKey(_ + _);
   result.collect

10.topK in spark
   val k = 2;
   val bcK = sc.broadcast(k);
   val input = sc.textFile("wordCount.txt");
   val words = input.flatMap(line => line.split(" "));
   val wordsNum = words.map(word => (word,1));
   class TKPartitioner(partitons:Int) extends org.apache.spark.Partitioner {
      def numPartitions:Int = partitons;
      def getPartition(key:Any):Int = {
           (key.hashCode & Int.MaxValue) % numPartitions;
      }
   }

   val wordsReduced = wordsNum.reduceByKey(_ + _);
   val wordsPartitioned = wordsReduced.partitionBy(new TKPartitioner(8));

   def findK(i:Iterable[(string，Int)]):Iterable[(string，Int) = {
	   val k = bcK.value;
	   val mark = new Array[(String,Int)](k);
	   while(i.hasNext){
		...
	   }	
   }

   val ked = wordsPartitioned.mapPartitions(findK);
   val result = findK(ked.collect);

11.partiton y有什么benefits
						
12.那些操作能够保持partitioner?(不改变key的)；
						
13.PageRank in spark: &&&&
   //links:A B C D
	   B C A
	   C D A
	   D B A C
    val input = sc.textFile("xxx");
    val links = input.map(process(line));//(A,Array(B,C,D))
    var ranks = links.mapValues(v=>1);
    for(i <- 1 to iterNum){
    	val joined = links.join(ranks); //(A,(Array(B,C,D),1))
	val contribution = joined.flatMap({
		e => val seq = e._2._1;
		     return seq.map(e._2._2*0.85+0.15);
	});
	ranks = contribution.reduceByKey(v => v+v);
    }

14. spark boradCast 的最大值是多少？ (数组的最大长度是个Int,Int.MaxValue=2GB)						

15. spark和hadoop的相同和区别
    相同：1> 都是基于 map-reduc 数据流思想
    不同: 1> hadoop 操作子只有map-reduce；spark 则很丰富；
	 2>  hadoop 和spark的作业调度系统不同
	     spark 将Application 划分为多个job (Actions)-> stages(dependency) -> tasks (partitions)
	 3> spark 尽量不写磁盘，用lineage 容错；
 	 4> shuffle 的不同 ，spark中避免排序
16.spark 的shuffle 过程

17.spark RDD中的shuffle算子

      3.1 去重：

　　　def distinct()

　　　def distinct(numPartitions: Int)

　　　

      3.2 聚合

　　　def reduceByKey(func: (V, V) => V, numPartitions: Int): RDD[(K, V)]

　　　def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)]

　　　def groupBy[K](f: T => K, p: Partitioner):RDD[(K, Iterable[V])]

　　　def groupByKey(partitioner: Partitioner):RDD[(K, Iterable[V])]

　　　def aggregateByKey[U: ClassTag](zeroValue: U, partitioner: Partitioner): RDD[(K, U)]

　　　def aggregateByKey[U: ClassTag](zeroValue: U, numPartitions: Int): RDD[(K, U)]

　　　def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C): RDD[(K, C)]

　　　def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, numPartitions: Int): RDD[(K, C)]

　　　def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, partitioner: Partitioner, mapSideCombine: Boolean = true, serializer: Serializer = null): RDD[(K, C)]

 

      3.3 排序

　　　def sortByKey(ascending: Boolean = true, numPartitions: Int = self.partitions.length): RDD[(K, V)]

 　　  def sortBy[K](f: (T) => K, ascending: Boolean = true, numPartitions: Int = this.partitions.length)(implicit ord: Ordering[K], ctag: ClassTag[K]): RDD[T]

 

      3.4 重分区

　　　def coalesce(numPartitions: Int, shuffle: Boolean = false, partitionCoalescer: Option[PartitionCoalescer] = Option.empty)

　　　def repartition(numPartitions: Int)(implicit ord: Ordering[T] = null)

 

      3.5集合或者表操作

　　　def intersection(other: RDD[T]): RDD[T]

　　　def intersection(other: RDD[T], partitioner: Partitioner)(implicit ord: Ordering[T] = null): RDD[T]

 　　  def intersection(other: RDD[T], numPartitions: Int): RDD[T]

　　　def subtract(other: RDD[T], numPartitions: Int): RDD[T]

　　　def subtract(other: RDD[T], p: Partitioner)(implicit ord: Ordering[T] = null): RDD[T]

　　　def subtractByKey[W: ClassTag](other: RDD[(K, W)]): RDD[(K, V)]

　　　def subtractByKey[W: ClassTag](other: RDD[(K, W)], numPartitions: Int): RDD[(K, V)]

　　　def subtractByKey[W: ClassTag](other: RDD[(K, W)], p: Partitioner): RDD[(K, V)]

　　　def join[W](other: RDD[(K, W)], partitioner: Partitioner): RDD[(K, (V, W))]

　　　def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]

　　　def join[W](other: RDD[(K, W)], numPartitions: Int): RDD[(K, (V, W))]

　　　def leftOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
