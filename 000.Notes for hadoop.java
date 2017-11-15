0. Hadoop 如何设置Map的数量

1. SecondarySort with MapReduce

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
       
     4> public class SSKMapper extends Mapper<LongWritable,Text,SSKey,Text> {
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

2. DistributedCacheFile in hadoop;
   1> Job job = Job.getInstance(new JobConf());
      job.addCacheFile("hdfs://myhdfs/distrubutedcachefile");

   2> setup in mapper/reducer:
      public class IMapper extends Mapper(LongWritable,Text,Object,Text){
         
         @override
         public void setup(Contex context) throws IOException{
            URI[] FileUris = context.getLocalCachedFiles();
            for(URI uri:FileUris){
               File file = new File(uri.getName);
               FileReader fr = new FireReader(file);
               BufferReader bf = new BufferReader(fr);
               String line = "";
               while((line=bf.readLine())!=null){
                  //...
               }
            }
         }
      }
     
3.Map Side Join
   1> Job job = Job.getInstance(getConf(),"");
      job.addCacheFile("hdfs://myhdfs/distrubutedcachefile");
      
      job.setJarByClass(LogProcessor.class);
      job.setMapperClass(joinMapper.class);
      job.setReduceNum(0);

      job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

      //job.setOutputKeyClass(Text.class);
		//job.setOutputValueClass(IntWritable.class);
		//job.setInputFormatClass(LongInputFormat.class);
		//job.setOutputFormatClass(TextOutputFormat.class);
		
      FileInputFormat.setInputPaths(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		
   
   2> public class joinMapper extends Mapper(LongWritable,Text,Text,Text){
         
         HashTable<String,Array<String>> hashTable  = new HashTable<String,Array<String>>();
         
         @override
         public void setup(Contex context) throws IOException{
            URI[] FileUris = context.getLocalCachedFiles();
            for(URI uri:FileUris){
               File file = new File(uri.getName);
               FileReader fr = new FireReader(file);
               BufferReader bf = new BufferReader(fr);
               String line = "";
               while((line=bf.readLine())!=null){
                  //fillInHashMap();
                  //...
               }
            }
         }
      
        @override
        public void map(LongWritable key,Text key,Context comtext){
           ....
        }
      }
3.Hadoop的Shuffle 过程

  1> http://www.cnblogs.com/acSzz/p/6383618.html
  2> http://blog.csdn.net/gyflyx/article/details/16831015

4.怎么写一个FileInputFormat
  1>. https://github.com/wallacegui/Mapreduce-CookBook/tree/master/chapter4/src/chapter4/inputformat
5.怎么写一个FileOutputFomat
  2>.
	
6.如何合并hdfs上的小文件
  1> filecrush

7.join的各种方式：
  1> map side join
  2> reduce side join ; => 如何通过secondaryKey优化
  3> semi Join
  4> 数据倾斜的 join => high-cardinality的key拿出来做mapsidejoin

8.key 倾斜导致的部分reducer完成困难如何解决？
