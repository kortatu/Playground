module Geometry
( Point(..)
, Segment(..)
, Line(..)
, Triangle(..)
, Direction(..)
, Semiplane(..)
, horizontalSort
, verticalSort
, slope
, lineFrom2Points
, lineFromSegment
, lineEquation
, sortSemiPlanes
, inLine
, distance
, distanceP
, area
, inTriangle
, sortSemiPlaneList
, sortSemiPlaneTriangle
, barycenters
, inSemiplaneStrict
, inSemiplane
, semiPlaneS
, semiPlaneT
) where 
  
import Data.List
import Data.Typeable

class Distanceable a where
    distance                    :: a -> Point -> Float

data Point = Point Float Float deriving (Show) 
data Line = Line Float Float Float deriving (Show)
data Triangle = Triangle Point Point Point deriving (Show)
data Segment = Segment Point Point deriving (Show)
data Direction = High | Low deriving (Show)
data Semiplane = Semiplane Line Direction deriving (Show)

instance Distanceable Point where
    distance p q = distanceP p q

instance Distanceable Line where
    distance l p = distanceL l p

-- POINT functions
horizontalSort (Point x _) (Point x' _) | x<=x' = LT
					| otherwise = GT
verticalSort (Point _ y) (Point _ y')   | y<=y' = LT
					| otherwise = GT

slope :: Point -> Point -> Float
slope (Point x y) (Point x' y') = (y' - y) / (x' - x)

-- SEGMENTS functions
lineFromSegment :: Segment -> Line
lineFromSegment (Segment p1 p2) = lineFrom2Points p1 p2

-- LINE functions
lineFrom2Points :: Point -> Point -> Line
lineFrom2Points p1@(Point x y) p2@(Point x' y') 
  | x/=x' = Line slope' 1 (y-slope'*x)
  | x==x' = Line 1 0 (-x)
  where slope' = slope p1 p2

lineEquation :: Line -> Float -> Float
lineEquation (Line a b c) x = (a*x+c) / b


sortSemiPlanes :: Line -> Point -> Ordering
sortSemiPlanes (Line _ 0 c) (Point x y) = compare x (negate c)
sortSemiPlanes line (Point x y) | lineEquation line x <  y = GT
				| lineEquation line x == y = EQ
				| lineEquation line x >  y = LT

inLine :: Line -> Point -> Bool
inLine line point = sortSemiPlanes line point == EQ

distanceL :: Line -> Point -> Float
distanceL (Line a b c) (Point x0 y0) = abs (a*x0 - b*y0 +c) / sqrt (a**2 + b**2)

distanceP :: Point -> Point -> Float
distanceP (Point x y) (Point x' y') = sqrt ((x-x')**2 + (y-y')**2)

-- DIRECTION functions
fromDirection :: Direction -> Ordering
fromDirection High = GT
fromDirection Low  = LT

fromOrdering GT = High
fromOrdering LT = Low

opp High = Low
opp Low = High

-- SEMIPLANE functions
inSemiplane :: Semiplane -> Point -> Bool
inSemiplane sp@(Semiplane line direction) point = order == EQ || order == fromDirection direction
    where order = sortSemiPlanes line point

inSemiplaneStrict ::  Semiplane -> Point -> Bool
inSemiplaneStrict (Semiplane line direction) point = sortSemiPlanes line point == fromDirection direction

semiPlaneS :: Segment -> Direction -> Semiplane
semiPlaneS s dir = Semiplane (lineFromSegment s) dir

semiPlaneT p1 p2 p3 = semiPlaneS seg dir
    where dir = opp (fromOrdering (sortSemiPlanes (lineFromSegment seg) p3))
          seg = (Segment p1 p2)

-- TRIANGLE functions
area :: Triangle -> Float
area (Triangle (Point x1 y1) (Point x2 y2) (Point x3 y3)) = (x1 *(y2-y3) + x2 *(y3-y1) + x3*(y1-y2)) / 2

barycenters :: Triangle -> [(Line, Point)]
barycenters (Triangle p1 p2 p3) = [(lineFrom2Points p1 p2,p3),(lineFrom2Points p2 p3,p1),(lineFrom2Points p1 p3,p2)]

inTriangle :: Triangle -> Point -> Bool
inTriangle t p0 = res1 == res2
   where res1 = sortSemiList bary
	 res2 = sortSemiList pointBary
	 pointBary = map (\x -> (fst x,p0)) bary
	 bary = barycenters t
	 sortSemiList x = map (uncurry sortSemiPlanes) x

sortSemiPlaneTriangle :: Triangle -> [Ordering]
sortSemiPlaneTriangle t = map (uncurry sortSemiPlanes) (barycenters t)

sortSemiPlaneList :: Triangle -> Point -> [Ordering]
sortSemiPlaneList t p = map (uncurry sortSemiPlanes) pointBary
  where pointBary = map (\x -> (fst x,p)) (barycenters t)
