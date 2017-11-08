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
     
