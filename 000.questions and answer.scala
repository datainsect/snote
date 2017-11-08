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

8.SecondarySort with MapReduce

   1> public class SSKey implements WritableComparable<SSKey> {
         private T fstKey;
         private V sndKey;
         
         public readFiles(DataInput in) throw IOException{
            fstkey.readFiles(in);
            sndKey.readFiles(in);
         }
         
         public write(DataOutput out) throw IOException{
            fstKey.write(out);
            sndKey.write(out);
         }
         
         public int compareto(SSKey o1){
            if(fstKey.equals(o1.fstKey)){
               return sndKey.compareTo(o1.sndKey);
            }
            return fstKey.compareTo(o1.sndKey);
         }
         
         public Boolean equals(Object o) {
            if(o1 instanceof SSKey){
               SSKey o1 = (SSKey) o;
               return this.fskKey.equals(o1.fstKey) && this.sndKey.equals(o1.sndKey);
            }
            return false;
         }
         
         public int hashcode() {
            return fstKey.hashcode() * 126 + sndKey.hashCode();
         }
      }
    2> public class SSPartitioner extends Partitoner<SSKey,Text> {
         
        @override
        public int getPartiton(SSKey key,Text v,int partitionNums){
            return (key.fstKey.hashcode() & Integr.MaxValue ) % partitionNums;
         }
       }
    3> public class GroupingComparator extends WritableComparator {
         
        @override
        public int compareTo(WritableComparator  o1,WritableComparator  o2) {
            SSKey key1 = (SSKey) o1;
            SSKey key2 = (SSKey) o2;
            return key1.sndKey.compareTo(key2.sndKey);
        }
       }
       
     4> public class SSKMapper extends Mapper<Object,Text,SSKey,Text> {
         public static SSKey ssk = new SSKey();
         @override
         public void map(Object k,Text v,Context context){
            String str = v.toString().split("\t");
            ssk.set(str(0),str(1));
            context.write(ssk,v);
         }
        }
     5> public class SSKReducer extends Reducer<SSKey,Text,NullWritable,Text>{
     
         @override
         public void reduce(SSKey key,Iterable<Text> values,Context context){
            for(Text t:values){
               Context(key.getFst(),t);
            }
         }
       }
     
