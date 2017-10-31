//// start of class point
//// start of class point
class Point(_x:Double,_y:Double,_id:Long=0,_centerId:Long=0) extends java.io.Serializable{
   val x = _x;
   val y = _y;
   val id = _id;
   var centerId = _centerId;
   override def toString = x +"\t"+y +"\t"+id+"\t"+centerId;
}

def distance(p1:Point,p2:Point):Double = {
  math.pow((p1.x-p2.x),2) + math.pow((p1.y-p2.y),2);
}

def assignTocenters(point:Point,centers:Array[Point]):Point = {
  println("running")
  val center = centers.minBy(distance(_,point));
  if(point.centerId!=center.id) println("centerChanged");
  point.centerId = center.id;
  return point
}

def sum(p1:Point,p2:Point):Point = new Point(p1.x+p2.x,p1.y+p2.y,0,p1.centerId);
def devide(p1:Point,num:Long) = new Point(p1.x/num,p1.y/num,p1.centerId,p1.centerId);

//// end of class point


val inputs = sc.textFile("2DMatrix.txt").filter(_.length>0).zipWithIndex;
var points = inputs.map{
    e =>
      val splited = e._1.split("\t");
      new Point(splited(0).toDouble,splited(1).toDouble,e._2)
}

val minPoint = points.reduce{
  (p1,p2) => new Point(math.min(p1.x,p2.x),math.min(p1.y,p2.y))
};

val maxPoint = points.reduce{
  (p1,p2) => new Point(math.max(p1.x,p2.x),math.max(p1.y,p2.y))
};


val centerNums = 3;
val maxIter = 500;
def initialCenters(centerNums:Int,minPoint:Point,maxPoint:Point):Array[Point] = {
  val centers = new Array[Point](centerNums);
  val stepX = (maxPoint.x - minPoint.x)/centerNums;
  val stepY = (maxPoint.y - minPoint.y)/centerNums;

  for(i<- 0 until centerNums){
    val x = (i+ math.random) * stepX + minPoint.x;
    val y = (i+ math.random) * stepY + minPoint.y;
    centers(i) = new Point(x,y,i);
  }
  return centers;
}

var centers = sc.broadcast(initialCenters(centerNums,minPoint,maxPoint));
var i = 0 ;
var changed = Long.MaxValue;
var pointsNum = points.count;
var previous = points.map(point =>(point.id,point.centerId));
while(i<maxIter && changed > 3){
   points = points.map(assignTocenters(_,centers.value));
   val id_centerId = points.map(point =>(point.id,point.centerId));
   val unioned = previous.union(id_centerId).distinct;
   changed = unioned.count - pointsNum;
   val keyByCenter = points.map(point => (point.centerId,(point,1)));
   val centersSumRdd  = keyByCenter.reduceByKey((p1,p2) => (sum(p1._1,p2._1),p1._2+p2._2));
   val centersRdd = centersSumRdd.map(_._2).map(p => devide(p._1,p._2));
   centers = sc.broadcast(centersRdd.collect.toArray);
   
   i += 1;
   println("round:" +  i+"  changed:" + changed)
}

///in local scala
import scala.collection.mutable.ArrayBuffer

class Point(_x:Double,_y:Double,_id:Long=0,_centerId:Long=0) extends java.io.Serializable{
   val x = _x;
   val y = _y;
   var id = _id;
   var centerId = _centerId;
   override def toString = x +"\t"+y +"\t"+id+"\t"+centerId;
}

def distance(p1:Point,p2:Point):Double = {
  math.pow((p1.x-p2.x),2) + math.pow((p1.y-p2.y),2);
}

def assignTocenters(point:Point,centers:Array[Point]):Point = {
  val center = centers.minBy(distance(_,point));
  point.centerId = center.id;
  return point
}

def sum(p1:Point,p2:Point):Point = new Point(p1.x+p2.x,p1.y+p2.y,0,p1.centerId);
def devide(p1:Point,num:Long) = new Point(p1.x/num,p1.y/num,p1.centerId,p1.centerId);
def avg(points:Array[Point]) = {
    val d = 2;
    val ini : Point = new Point(0,0,0,points(0).centerId);
    var point = points.fold(ini)(sum)
    devide(point,points.length);
  }
///endOfkmeans

def loadDataSet2Points(filePath:String):Array[Point] = {
    var dataMat = new ArrayBuffer[Point]();
    var lines = scala.io.Source.fromFile(filePath).getLines.toArray.zipWithIndex;
    lines.map{
      e =>
        val splited = e._1.split("\t");
        new Point(splited(0).toDouble,splited(1).toDouble,e._2);
    }
}

val inputPath = "2DMatrix.txt";
var points = loadDataSet2Points(inputPath)

val minPoint = points.reduce{
  (p1,p2) => new Point(math.min(p1.x,p2.x),math.min(p1.y,p2.y))
};

val maxPoint = points.reduce{
  (p1,p2) => new Point(math.max(p1.x,p2.x),math.max(p1.y,p2.y))
};


val centerNums = 3;
val maxIter = 500;
def initialCenters(centerNums:Int,minPoint:Point,maxPoint:Point):Array[Point] = {
  val centers = new Array[Point](centerNums);
  val stepX = (maxPoint.x - minPoint.x)/centerNums;
  val stepY = (maxPoint.y - minPoint.y)/centerNums;

  for(i<- 0 until centerNums){
    val x = (i+ math.random) * stepX + minPoint.x;
    val y = (i+ math.random) * stepY + minPoint.y;
    centers(i) = new Point(x,y,i);
  }
  return centers;
}

var centers = initialCenters(centerNums,minPoint,maxPoint);


var i = 0 ;
var changed = Long.MaxValue;
var pointsNum = points.length;

while(i<maxIter && changed > 3){
   val previous = points;
   points = points.map(point => assignTocenters(point,centers));
   val keyByCenter = points.groupBy(_.centerId);
   centers = centers.map(
      center =>{
        val newCenter = avg(keyByCenter(center.id));
        newCenter.id = center.id;
        newCenter;
      }
   );

   val unioned = points.map(point=>(point.id,point.centerId))++(previous.map(point=>(point.id,point.centerId)))
   val num = unioned.groupBy(p => p).toList.length;
   changed = num.length - pointsNum;
   i += 1;
   println("round:" +  i+"  changed:" + changed)
}
