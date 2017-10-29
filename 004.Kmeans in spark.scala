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
  var minDistance = distance(point,centers(point.centerId.toInt));
  for(center <- centers){
    val distc = distance(point,center);
    if(distc<minDistance){
      minDistance = distc;
      point.centerId = center.id;
    }
  }
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


val centerNums = 50;
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
while(i<maxIter && changed > 3){
   val previous = points;
   points = points.map(point => assignTocenters(point,centers.value));
   val keyByCenter = points.map(point => (point.centerId,(point,1)));
   val centersSumRdd  = keyByCenter.reduceByKey((p1,p2) => (sum(p1._1,p2._1),p1._2+p2._2));
   val centersRdd = centersSumRdd.map(_._2).map(p => devide(p._1,p._2));
   centers = sc.broadcast(centersRdd.collect.toArray);
   val unioned = points.map(point=>(point.id,point.centerId)).union(previous.map(point=>(point.id,point.centerId)))
   val num = unioned.keyBy(p=>p).reduceByKey((p1,p2)=>p1);
   changed = num.count - pointsNum;
   i += 1;
   println("round:" +  i+"  changed:" + changed)
}
