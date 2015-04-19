<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE eagle SYSTEM "eagle.dtd">
<eagle version="6.3">
<drawing>
<settings>
<setting alwaysvectorfont="no"/>
<setting verticaltext="up"/>
</settings>
<grid distance="0.1" unitdist="inch" unit="inch" style="lines" multiple="1" display="no" altdistance="0.01" altunitdist="inch" altunit="inch"/>
<layers>
<layer number="1" name="Top" color="4" fill="1" visible="no" active="no"/>
<layer number="2" name="Route2" color="1" fill="3" visible="no" active="no"/>
<layer number="3" name="Route3" color="4" fill="3" visible="no" active="no"/>
<layer number="4" name="Route4" color="1" fill="4" visible="no" active="no"/>
<layer number="5" name="Route5" color="4" fill="4" visible="no" active="no"/>
<layer number="6" name="Route6" color="1" fill="8" visible="no" active="no"/>
<layer number="7" name="Route7" color="4" fill="8" visible="no" active="no"/>
<layer number="8" name="Route8" color="1" fill="2" visible="no" active="no"/>
<layer number="9" name="Route9" color="4" fill="2" visible="no" active="no"/>
<layer number="10" name="Route10" color="1" fill="7" visible="no" active="no"/>
<layer number="11" name="Route11" color="4" fill="7" visible="no" active="no"/>
<layer number="12" name="Route12" color="1" fill="5" visible="no" active="no"/>
<layer number="13" name="Route13" color="4" fill="5" visible="no" active="no"/>
<layer number="14" name="Route14" color="1" fill="6" visible="no" active="no"/>
<layer number="15" name="Route15" color="4" fill="6" visible="no" active="no"/>
<layer number="16" name="Bottom" color="1" fill="1" visible="no" active="no"/>
<layer number="17" name="Pads" color="2" fill="1" visible="no" active="no"/>
<layer number="18" name="Vias" color="2" fill="1" visible="no" active="no"/>
<layer number="19" name="Unrouted" color="6" fill="1" visible="no" active="no"/>
<layer number="20" name="Dimension" color="15" fill="1" visible="no" active="no"/>
<layer number="21" name="tPlace" color="7" fill="1" visible="no" active="no"/>
<layer number="22" name="bPlace" color="7" fill="1" visible="no" active="no"/>
<layer number="23" name="tOrigins" color="15" fill="1" visible="no" active="no"/>
<layer number="24" name="bOrigins" color="15" fill="1" visible="no" active="no"/>
<layer number="25" name="tNames" color="7" fill="1" visible="no" active="no"/>
<layer number="26" name="bNames" color="7" fill="1" visible="no" active="no"/>
<layer number="27" name="tValues" color="7" fill="1" visible="no" active="no"/>
<layer number="28" name="bValues" color="7" fill="1" visible="no" active="no"/>
<layer number="29" name="tStop" color="7" fill="3" visible="no" active="no"/>
<layer number="30" name="bStop" color="7" fill="6" visible="no" active="no"/>
<layer number="31" name="tCream" color="7" fill="4" visible="no" active="no"/>
<layer number="32" name="bCream" color="7" fill="5" visible="no" active="no"/>
<layer number="33" name="tFinish" color="6" fill="3" visible="no" active="no"/>
<layer number="34" name="bFinish" color="6" fill="6" visible="no" active="no"/>
<layer number="35" name="tGlue" color="7" fill="4" visible="no" active="no"/>
<layer number="36" name="bGlue" color="7" fill="5" visible="no" active="no"/>
<layer number="37" name="tTest" color="7" fill="1" visible="no" active="no"/>
<layer number="38" name="bTest" color="7" fill="1" visible="no" active="no"/>
<layer number="39" name="tKeepout" color="4" fill="11" visible="no" active="no"/>
<layer number="40" name="bKeepout" color="1" fill="11" visible="no" active="no"/>
<layer number="41" name="tRestrict" color="4" fill="10" visible="no" active="no"/>
<layer number="42" name="bRestrict" color="1" fill="10" visible="no" active="no"/>
<layer number="43" name="vRestrict" color="2" fill="10" visible="no" active="no"/>
<layer number="44" name="Drills" color="7" fill="1" visible="no" active="no"/>
<layer number="45" name="Holes" color="7" fill="1" visible="no" active="no"/>
<layer number="46" name="Milling" color="3" fill="1" visible="no" active="no"/>
<layer number="47" name="Measures" color="7" fill="1" visible="no" active="no"/>
<layer number="48" name="Document" color="7" fill="1" visible="no" active="no"/>
<layer number="49" name="Reference" color="7" fill="1" visible="no" active="no"/>
<layer number="51" name="tDocu" color="7" fill="1" visible="no" active="no"/>
<layer number="52" name="bDocu" color="7" fill="1" visible="no" active="no"/>
<layer number="91" name="Nets" color="2" fill="1" visible="yes" active="yes"/>
<layer number="92" name="Busses" color="1" fill="1" visible="yes" active="yes"/>
<layer number="93" name="Pins" color="2" fill="1" visible="no" active="yes"/>
<layer number="94" name="Symbols" color="4" fill="1" visible="yes" active="yes"/>
<layer number="95" name="Names" color="7" fill="1" visible="yes" active="yes"/>
<layer number="96" name="Values" color="7" fill="1" visible="yes" active="yes"/>
<layer number="97" name="Info" color="7" fill="1" visible="yes" active="yes"/>
<layer number="98" name="Guide" color="6" fill="1" visible="yes" active="yes"/>
</layers>
<schematic xreflabel="%F%N/%S.%C%R" xrefpart="/%S.%C%R">
<libraries>
<library name="con-lstb">
<description>&lt;b&gt;Pin Headers&lt;/b&gt;&lt;p&gt;
Naming:&lt;p&gt;
MA = male&lt;p&gt;
# contacts - # rows&lt;p&gt;
W = angled&lt;p&gt;
&lt;author&gt;Created by librarian@cadsoft.de&lt;/author&gt;</description>
<packages>
<package name="MA06-1">
<description>&lt;b&gt;PIN HEADER&lt;/b&gt;</description>
<wire x1="-6.985" y1="1.27" x2="-5.715" y2="1.27" width="0.1524" layer="21"/>
<wire x1="-5.715" y1="1.27" x2="-5.08" y2="0.635" width="0.1524" layer="21"/>
<wire x1="-5.08" y1="-0.635" x2="-5.715" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-5.08" y1="0.635" x2="-4.445" y2="1.27" width="0.1524" layer="21"/>
<wire x1="-4.445" y1="1.27" x2="-3.175" y2="1.27" width="0.1524" layer="21"/>
<wire x1="-3.175" y1="1.27" x2="-2.54" y2="0.635" width="0.1524" layer="21"/>
<wire x1="-2.54" y1="-0.635" x2="-3.175" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-3.175" y1="-1.27" x2="-4.445" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-4.445" y1="-1.27" x2="-5.08" y2="-0.635" width="0.1524" layer="21"/>
<wire x1="-7.62" y1="0.635" x2="-7.62" y2="-0.635" width="0.1524" layer="21"/>
<wire x1="-6.985" y1="1.27" x2="-7.62" y2="0.635" width="0.1524" layer="21"/>
<wire x1="-7.62" y1="-0.635" x2="-6.985" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-5.715" y1="-1.27" x2="-6.985" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-2.54" y1="0.635" x2="-1.905" y2="1.27" width="0.1524" layer="21"/>
<wire x1="-1.905" y1="1.27" x2="-0.635" y2="1.27" width="0.1524" layer="21"/>
<wire x1="-0.635" y1="1.27" x2="0" y2="0.635" width="0.1524" layer="21"/>
<wire x1="0" y1="-0.635" x2="-0.635" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-0.635" y1="-1.27" x2="-1.905" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-1.905" y1="-1.27" x2="-2.54" y2="-0.635" width="0.1524" layer="21"/>
<wire x1="0.635" y1="1.27" x2="1.905" y2="1.27" width="0.1524" layer="21"/>
<wire x1="1.905" y1="1.27" x2="2.54" y2="0.635" width="0.1524" layer="21"/>
<wire x1="2.54" y1="-0.635" x2="1.905" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="2.54" y1="0.635" x2="3.175" y2="1.27" width="0.1524" layer="21"/>
<wire x1="3.175" y1="1.27" x2="4.445" y2="1.27" width="0.1524" layer="21"/>
<wire x1="4.445" y1="1.27" x2="5.08" y2="0.635" width="0.1524" layer="21"/>
<wire x1="5.08" y1="-0.635" x2="4.445" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="4.445" y1="-1.27" x2="3.175" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="3.175" y1="-1.27" x2="2.54" y2="-0.635" width="0.1524" layer="21"/>
<wire x1="0.635" y1="1.27" x2="0" y2="0.635" width="0.1524" layer="21"/>
<wire x1="0" y1="-0.635" x2="0.635" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="1.905" y1="-1.27" x2="0.635" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="5.08" y1="0.635" x2="5.715" y2="1.27" width="0.1524" layer="21"/>
<wire x1="5.715" y1="1.27" x2="6.985" y2="1.27" width="0.1524" layer="21"/>
<wire x1="6.985" y1="1.27" x2="7.62" y2="0.635" width="0.1524" layer="21"/>
<wire x1="7.62" y1="-0.635" x2="6.985" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="6.985" y1="-1.27" x2="5.715" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="5.715" y1="-1.27" x2="5.08" y2="-0.635" width="0.1524" layer="21"/>
<wire x1="7.62" y1="0.635" x2="7.62" y2="-0.635" width="0.1524" layer="21"/>
<pad name="1" x="-6.35" y="0" drill="1.016" shape="long" rot="R90"/>
<pad name="2" x="-3.81" y="0" drill="1.016" shape="long" rot="R90"/>
<pad name="3" x="-1.27" y="0" drill="1.016" shape="long" rot="R90"/>
<pad name="4" x="1.27" y="0" drill="1.016" shape="long" rot="R90"/>
<pad name="5" x="3.81" y="0" drill="1.016" shape="long" rot="R90"/>
<pad name="6" x="6.35" y="0" drill="1.016" shape="long" rot="R90"/>
<text x="-7.62" y="1.651" size="1.27" layer="25" ratio="10">&gt;NAME</text>
<text x="-6.985" y="-2.921" size="1.27" layer="21" ratio="10">1</text>
<text x="5.715" y="1.651" size="1.27" layer="21" ratio="10">6</text>
<text x="-2.54" y="-2.921" size="1.27" layer="27" ratio="10">&gt;VALUE</text>
<rectangle x1="-4.064" y1="-0.254" x2="-3.556" y2="0.254" layer="51"/>
<rectangle x1="-6.604" y1="-0.254" x2="-6.096" y2="0.254" layer="51"/>
<rectangle x1="-1.524" y1="-0.254" x2="-1.016" y2="0.254" layer="51"/>
<rectangle x1="3.556" y1="-0.254" x2="4.064" y2="0.254" layer="51"/>
<rectangle x1="1.016" y1="-0.254" x2="1.524" y2="0.254" layer="51"/>
<rectangle x1="6.096" y1="-0.254" x2="6.604" y2="0.254" layer="51"/>
</package>
</packages>
<symbols>
<symbol name="MA06-1">
<wire x1="3.81" y1="-10.16" x2="-1.27" y2="-10.16" width="0.4064" layer="94"/>
<wire x1="1.27" y1="-2.54" x2="2.54" y2="-2.54" width="0.6096" layer="94"/>
<wire x1="1.27" y1="-5.08" x2="2.54" y2="-5.08" width="0.6096" layer="94"/>
<wire x1="1.27" y1="-7.62" x2="2.54" y2="-7.62" width="0.6096" layer="94"/>
<wire x1="1.27" y1="2.54" x2="2.54" y2="2.54" width="0.6096" layer="94"/>
<wire x1="1.27" y1="0" x2="2.54" y2="0" width="0.6096" layer="94"/>
<wire x1="1.27" y1="5.08" x2="2.54" y2="5.08" width="0.6096" layer="94"/>
<wire x1="-1.27" y1="7.62" x2="-1.27" y2="-10.16" width="0.4064" layer="94"/>
<wire x1="3.81" y1="-10.16" x2="3.81" y2="7.62" width="0.4064" layer="94"/>
<wire x1="-1.27" y1="7.62" x2="3.81" y2="7.62" width="0.4064" layer="94"/>
<text x="-1.27" y="-12.7" size="1.778" layer="96">&gt;VALUE</text>
<text x="-1.27" y="8.382" size="1.778" layer="95">&gt;NAME</text>
<pin name="1" x="7.62" y="-7.62" visible="pad" length="middle" direction="pas" swaplevel="1" rot="R180"/>
<pin name="2" x="7.62" y="-5.08" visible="pad" length="middle" direction="pas" swaplevel="1" rot="R180"/>
<pin name="3" x="7.62" y="-2.54" visible="pad" length="middle" direction="pas" swaplevel="1" rot="R180"/>
<pin name="4" x="7.62" y="0" visible="pad" length="middle" direction="pas" swaplevel="1" rot="R180"/>
<pin name="5" x="7.62" y="2.54" visible="pad" length="middle" direction="pas" swaplevel="1" rot="R180"/>
<pin name="6" x="7.62" y="5.08" visible="pad" length="middle" direction="pas" swaplevel="1" rot="R180"/>
</symbol>
</symbols>
<devicesets>
<deviceset name="MA06-1" prefix="SV" uservalue="yes">
<description>&lt;b&gt;PIN HEADER&lt;/b&gt;</description>
<gates>
<gate name="1" symbol="MA06-1" x="0" y="0"/>
</gates>
<devices>
<device name="" package="MA06-1">
<connects>
<connect gate="1" pin="1" pad="1"/>
<connect gate="1" pin="2" pad="2"/>
<connect gate="1" pin="3" pad="3"/>
<connect gate="1" pin="4" pad="4"/>
<connect gate="1" pin="5" pad="5"/>
<connect gate="1" pin="6" pad="6"/>
</connects>
<technologies>
<technology name="">
<attribute name="MF" value="" constant="no"/>
<attribute name="MPN" value="" constant="no"/>
<attribute name="OC_FARNELL" value="unknown" constant="no"/>
<attribute name="OC_NEWARK" value="unknown" constant="no"/>
</technology>
</technologies>
</device>
</devices>
</deviceset>
</devicesets>
</library>
<library name="con-amp">
<description>&lt;b&gt;AMP Connectors&lt;/b&gt;&lt;p&gt;
RJ45 Jack connectors&lt;br&gt;
 Based on the previous libraris:
 &lt;ul&gt;
 &lt;li&gt;amp.lbr
 &lt;li&gt;amp-j.lbr
 &lt;li&gt;amp-mta.lbr
 &lt;li&gt;amp-nlok.lbr
 &lt;li&gt;amp-sim.lbr
 &lt;li&gt;amp-micro-match.lbr
 &lt;/ul&gt;
 Sources :
 &lt;ul&gt;
 &lt;li&gt;Catalog 82066 Revised 11-95 
 &lt;li&gt;Product Guide 296785 Rev. 8-99
 &lt;li&gt;Product Guide CD-ROM 1999
 &lt;li&gt;www.amp.com
 &lt;/ul&gt;
 &lt;author&gt;Created by librarian@cadsoft.de&lt;/author&gt;</description>
<packages>
<package name="10X03MTA">
<description>&lt;b&gt;AMP MTA connector&lt;/b&gt;&lt;p&gt;
Source: http://ecommas.tycoelectronics.com .. ENG_CD_640456_W.pdf</description>
<wire x1="-3.81" y1="-1.27" x2="-3.81" y2="1.27" width="0.1524" layer="21"/>
<wire x1="3.81" y1="1.27" x2="-3.81" y2="1.27" width="0.1524" layer="21"/>
<wire x1="-3.81" y1="-1.27" x2="3.81" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-3.81" y1="1.27" x2="-3.81" y2="1.905" width="0.1524" layer="21"/>
<wire x1="3.81" y1="1.905" x2="-3.81" y2="1.905" width="0.1524" layer="21"/>
<wire x1="3.81" y1="1.27" x2="3.81" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="3.81" y1="1.905" x2="3.81" y2="1.27" width="0.1524" layer="21"/>
<pad name="3" x="-2.54" y="0" drill="1.016" shape="long" rot="R90"/>
<pad name="2" x="0" y="0" drill="1.016" shape="long" rot="R90"/>
<pad name="1" x="2.54" y="0" drill="1.016" shape="long" rot="R90"/>
<text x="-2.6162" y="-3.2512" size="1.27" layer="25">&gt;NAME</text>
<text x="-3.7762" y="2.1509" size="1.27" layer="27">&gt;VALUE</text>
<rectangle x1="2.286" y1="-0.254" x2="2.794" y2="0.254" layer="21"/>
<rectangle x1="-0.254" y1="-0.254" x2="0.254" y2="0.254" layer="21"/>
<rectangle x1="-2.794" y1="-0.254" x2="-2.286" y2="0.254" layer="21"/>
</package>
</packages>
<symbols>
<symbol name="MTA-1_3">
<wire x1="-3.81" y1="1.27" x2="-3.81" y2="-1.905" width="0.254" layer="94"/>
<wire x1="3.81" y1="-1.905" x2="-3.81" y2="-1.905" width="0.254" layer="94"/>
<wire x1="3.81" y1="-1.905" x2="3.81" y2="1.27" width="0.254" layer="94"/>
<wire x1="-3.81" y1="1.27" x2="3.81" y2="1.27" width="0.254" layer="94"/>
<circle x="-2.54" y="0" radius="0.635" width="0.254" layer="94"/>
<circle x="0" y="0" radius="0.635" width="0.254" layer="94"/>
<circle x="2.54" y="0" radius="0.635" width="0.254" layer="94"/>
<text x="5.08" y="0" size="1.778" layer="95">&gt;NAME</text>
<text x="5.08" y="-3.81" size="1.778" layer="96">&gt;VALUE</text>
<text x="-5.08" y="-1.27" size="1.27" layer="95">1</text>
<pin name="1" x="-2.54" y="-2.54" visible="off" length="short" direction="pas" rot="R90"/>
<pin name="3" x="2.54" y="-2.54" visible="off" length="short" direction="pas" rot="R90"/>
<pin name="2" x="0" y="-2.54" visible="off" length="short" direction="pas" rot="R90"/>
</symbol>
</symbols>
<devicesets>
<deviceset name="MTA03-100" prefix="J" uservalue="yes">
<description>&lt;b&gt;AMP connector&lt;/b&gt;</description>
<gates>
<gate name="G$1" symbol="MTA-1_3" x="0" y="0"/>
</gates>
<devices>
<device name="" package="10X03MTA">
<connects>
<connect gate="G$1" pin="1" pad="1"/>
<connect gate="G$1" pin="2" pad="2"/>
<connect gate="G$1" pin="3" pad="3"/>
</connects>
<technologies>
<technology name=""/>
</technologies>
</device>
</devices>
</deviceset>
</devicesets>
</library>
<library name="pinhead">
<description>&lt;b&gt;Pin Header Connectors&lt;/b&gt;&lt;p&gt;
&lt;author&gt;Created by librarian@cadsoft.de&lt;/author&gt;</description>
<packages>
<package name="1X01">
<description>&lt;b&gt;PIN HEADER&lt;/b&gt;</description>
<wire x1="-0.635" y1="1.27" x2="0.635" y2="1.27" width="0.1524" layer="21"/>
<wire x1="0.635" y1="1.27" x2="1.27" y2="0.635" width="0.1524" layer="21"/>
<wire x1="1.27" y1="0.635" x2="1.27" y2="-0.635" width="0.1524" layer="21"/>
<wire x1="1.27" y1="-0.635" x2="0.635" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="-1.27" y1="0.635" x2="-1.27" y2="-0.635" width="0.1524" layer="21"/>
<wire x1="-0.635" y1="1.27" x2="-1.27" y2="0.635" width="0.1524" layer="21"/>
<wire x1="-1.27" y1="-0.635" x2="-0.635" y2="-1.27" width="0.1524" layer="21"/>
<wire x1="0.635" y1="-1.27" x2="-0.635" y2="-1.27" width="0.1524" layer="21"/>
<pad name="1" x="0" y="0" drill="1.016" shape="octagon"/>
<text x="-1.3462" y="1.8288" size="1.27" layer="25" ratio="10">&gt;NAME</text>
<text x="-1.27" y="-3.175" size="1.27" layer="27">&gt;VALUE</text>
<rectangle x1="-0.254" y1="-0.254" x2="0.254" y2="0.254" layer="51"/>
</package>
</packages>
<symbols>
<symbol name="PINHD1">
<wire x1="-6.35" y1="-2.54" x2="1.27" y2="-2.54" width="0.4064" layer="94"/>
<wire x1="1.27" y1="-2.54" x2="1.27" y2="2.54" width="0.4064" layer="94"/>
<wire x1="1.27" y1="2.54" x2="-6.35" y2="2.54" width="0.4064" layer="94"/>
<wire x1="-6.35" y1="2.54" x2="-6.35" y2="-2.54" width="0.4064" layer="94"/>
<text x="-6.35" y="3.175" size="1.778" layer="95">&gt;NAME</text>
<text x="-6.35" y="-5.08" size="1.778" layer="96">&gt;VALUE</text>
<pin name="1" x="-2.54" y="0" visible="pad" length="short" direction="pas" function="dot"/>
</symbol>
</symbols>
<devicesets>
<deviceset name="PINHD-1X1" prefix="JP" uservalue="yes">
<description>&lt;b&gt;PIN HEADER&lt;/b&gt;</description>
<gates>
<gate name="G$1" symbol="PINHD1" x="0" y="0"/>
</gates>
<devices>
<device name="" package="1X01">
<connects>
<connect gate="G$1" pin="1" pad="1"/>
</connects>
<technologies>
<technology name=""/>
</technologies>
</device>
</devices>
</deviceset>
</devicesets>
</library>
</libraries>
<attributes>
</attributes>
<variantdefs>
</variantdefs>
<classes>
<class number="0" name="default" width="0" drill="0">
</class>
</classes>
<parts>
<part name="SV1" library="con-lstb" deviceset="MA06-1" device=""/>
<part name="SV2" library="con-lstb" deviceset="MA06-1" device=""/>
<part name="J1" library="con-amp" deviceset="MTA03-100" device=""/>
<part name="JP1" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP2" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP3" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP4" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP5" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP6" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP7" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP8" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP9" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP10" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP11" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP12" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP13" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP14" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP15" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP16" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP17" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP18" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP19" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP20" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP21" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP22" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP23" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP24" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP25" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP26" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP27" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP28" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP29" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP30" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP31" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP32" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP33" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP34" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP35" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP36" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP37" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP38" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP39" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP40" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP41" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP42" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP43" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP44" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP45" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP46" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP47" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP48" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP49" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP50" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP51" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP52" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP53" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP54" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP55" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP56" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP57" library="pinhead" deviceset="PINHD-1X1" device=""/>
<part name="JP58" library="pinhead" deviceset="PINHD-1X1" device=""/>
</parts>
<sheets>
<sheet>
<plain>
</plain>
<instances>
<instance part="SV1" gate="1" x="0" y="20.32"/>
<instance part="SV2" gate="1" x="50.8" y="17.78" rot="R180"/>
<instance part="J1" gate="G$1" x="76.2" y="38.1"/>
<instance part="JP1" gate="G$1" x="22.86" y="78.74" rot="R270"/>
<instance part="JP2" gate="G$1" x="25.4" y="78.74" rot="R270"/>
<instance part="JP3" gate="G$1" x="22.86" y="73.66" rot="R90"/>
<instance part="JP4" gate="G$1" x="25.4" y="73.66" rot="R90"/>
<instance part="JP5" gate="G$1" x="27.94" y="73.66" rot="R90"/>
<instance part="JP6" gate="G$1" x="30.48" y="73.66" rot="R90"/>
<instance part="JP7" gate="G$1" x="33.02" y="73.66" rot="R90"/>
<instance part="JP8" gate="G$1" x="35.56" y="73.66" rot="R90"/>
<instance part="JP9" gate="G$1" x="38.1" y="73.66" rot="R90"/>
<instance part="JP10" gate="G$1" x="40.64" y="73.66" rot="R90"/>
<instance part="JP11" gate="G$1" x="43.18" y="73.66" rot="R90"/>
<instance part="JP12" gate="G$1" x="45.72" y="73.66" rot="R90"/>
<instance part="JP13" gate="G$1" x="48.26" y="73.66" rot="R90"/>
<instance part="JP14" gate="G$1" x="50.8" y="73.66" rot="R90"/>
<instance part="JP15" gate="G$1" x="27.94" y="78.74" rot="R270"/>
<instance part="JP16" gate="G$1" x="30.48" y="78.74" rot="R270"/>
<instance part="JP17" gate="G$1" x="33.02" y="78.74" rot="R270"/>
<instance part="JP18" gate="G$1" x="35.56" y="78.74" rot="R270"/>
<instance part="JP19" gate="G$1" x="38.1" y="78.74" rot="R270"/>
<instance part="JP20" gate="G$1" x="40.64" y="78.74" rot="R270"/>
<instance part="JP21" gate="G$1" x="43.18" y="78.74" rot="R270"/>
<instance part="JP22" gate="G$1" x="45.72" y="78.74" rot="R270"/>
<instance part="JP23" gate="G$1" x="48.26" y="78.74" rot="R270"/>
<instance part="JP24" gate="G$1" x="50.8" y="78.74" rot="R270"/>
<instance part="JP25" gate="G$1" x="83.82" y="78.74" rot="R270"/>
<instance part="JP26" gate="G$1" x="81.28" y="78.74" rot="R270"/>
<instance part="JP27" gate="G$1" x="78.74" y="78.74" rot="R270"/>
<instance part="JP28" gate="G$1" x="76.2" y="78.74" rot="R270"/>
<instance part="JP29" gate="G$1" x="73.66" y="78.74" rot="R270"/>
<instance part="JP30" gate="G$1" x="71.12" y="78.74" rot="R270"/>
<instance part="JP31" gate="G$1" x="83.82" y="73.66" rot="R90"/>
<instance part="JP32" gate="G$1" x="81.28" y="73.66" rot="R90"/>
<instance part="JP33" gate="G$1" x="78.74" y="73.66" rot="R90"/>
<instance part="JP34" gate="G$1" x="76.2" y="73.66" rot="R90"/>
<instance part="JP35" gate="G$1" x="73.66" y="73.66" rot="R90"/>
<instance part="JP36" gate="G$1" x="71.12" y="73.66" rot="R90"/>
<instance part="JP37" gate="G$1" x="83.82" y="119.38" rot="R90"/>
<instance part="JP38" gate="G$1" x="81.28" y="119.38" rot="R90"/>
<instance part="JP39" gate="G$1" x="78.74" y="119.38" rot="R90"/>
<instance part="JP40" gate="G$1" x="76.2" y="119.38" rot="R90"/>
<instance part="JP41" gate="G$1" x="73.66" y="119.38" rot="R90"/>
<instance part="JP42" gate="G$1" x="71.12" y="119.38" rot="R90"/>
<instance part="JP43" gate="G$1" x="68.58" y="119.38" rot="R90"/>
<instance part="JP44" gate="G$1" x="66.04" y="119.38" rot="R90"/>
<instance part="JP45" gate="G$1" x="63.5" y="119.38" rot="R90"/>
<instance part="JP46" gate="G$1" x="60.96" y="119.38" rot="R90"/>
<instance part="JP47" gate="G$1" x="58.42" y="119.38" rot="R90"/>
<instance part="JP48" gate="G$1" x="83.82" y="124.46" rot="R270"/>
<instance part="JP49" gate="G$1" x="81.28" y="124.46" rot="R270"/>
<instance part="JP50" gate="G$1" x="78.74" y="124.46" rot="R270"/>
<instance part="JP51" gate="G$1" x="76.2" y="124.46" rot="R270"/>
<instance part="JP52" gate="G$1" x="73.66" y="124.46" rot="R270"/>
<instance part="JP53" gate="G$1" x="71.12" y="124.46" rot="R270"/>
<instance part="JP54" gate="G$1" x="68.58" y="124.46" rot="R270"/>
<instance part="JP55" gate="G$1" x="66.04" y="124.46" rot="R270"/>
<instance part="JP56" gate="G$1" x="63.5" y="124.46" rot="R270"/>
<instance part="JP57" gate="G$1" x="60.96" y="124.46" rot="R270"/>
<instance part="JP58" gate="G$1" x="58.42" y="124.46" rot="R270"/>
</instances>
<busses>
</busses>
<nets>
<net name="LV2" class="0">
<segment>
<pinref part="SV1" gate="1" pin="2"/>
<wire x1="7.62" y1="15.24" x2="10.16" y2="15.24" width="0.1524" layer="91"/>
<label x="10.16" y="15.24" size="1.778" layer="95" xref="yes"/>
</segment>
</net>
<net name="GND" class="0">
<segment>
<pinref part="SV2" gate="1" pin="4"/>
<wire x1="43.18" y1="17.78" x2="40.64" y2="17.78" width="0.1524" layer="91"/>
<label x="40.64" y="17.78" size="1.778" layer="95" rot="R180" xref="yes"/>
</segment>
<segment>
<pinref part="J1" gate="G$1" pin="1"/>
<wire x1="73.66" y1="35.56" x2="73.66" y2="33.02" width="0.1524" layer="91"/>
<label x="73.66" y="33.02" size="1.778" layer="95" rot="R270" xref="yes"/>
</segment>
<segment>
<wire x1="83.82" y1="127" x2="83.82" y2="132.08" width="0.1524" layer="91"/>
<label x="83.82" y="132.08" size="1.778" layer="95" rot="R90" xref="yes"/>
<pinref part="JP48" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="83.82" y1="116.84" x2="83.82" y2="111.76" width="0.1524" layer="91"/>
<label x="83.82" y="111.76" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP37" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="78.74" y1="127" x2="78.74" y2="132.08" width="0.1524" layer="91"/>
<label x="78.74" y="132.08" size="1.778" layer="95" rot="R90" xref="yes"/>
<pinref part="JP50" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="83.82" y1="81.28" x2="83.82" y2="86.36" width="0.1524" layer="91"/>
<label x="83.82" y="86.36" size="1.778" layer="95" rot="R90" xref="yes"/>
<pinref part="JP25" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="83.82" y1="71.12" x2="83.82" y2="66.04" width="0.1524" layer="91"/>
<label x="83.82" y="66.04" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP31" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="22.86" y1="81.28" x2="22.86" y2="86.36" width="0.1524" layer="91"/>
<label x="22.86" y="86.36" size="1.778" layer="95" rot="R90" xref="yes"/>
<pinref part="JP1" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="22.86" y1="71.12" x2="22.86" y2="66.04" width="0.1524" layer="91"/>
<label x="22.86" y="66.04" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP3" gate="G$1" pin="1"/>
</segment>
<segment>
<pinref part="SV1" gate="1" pin="4"/>
<wire x1="7.62" y1="20.32" x2="10.16" y2="20.32" width="0.1524" layer="91"/>
<label x="10.16" y="20.32" size="1.778" layer="95" xref="yes"/>
</segment>
</net>
<net name="LV3" class="0">
<segment>
<pinref part="SV1" gate="1" pin="5"/>
<wire x1="7.62" y1="22.86" x2="10.16" y2="22.86" width="0.1524" layer="91"/>
<label x="10.16" y="22.86" size="1.778" layer="95" xref="yes"/>
</segment>
</net>
<net name="HV2" class="0">
<segment>
<pinref part="SV2" gate="1" pin="2"/>
<wire x1="43.18" y1="22.86" x2="40.64" y2="22.86" width="0.1524" layer="91"/>
<label x="40.64" y="22.86" size="1.778" layer="95" rot="R180" xref="yes"/>
</segment>
</net>
<net name="HV3" class="0">
<segment>
<pinref part="SV2" gate="1" pin="5"/>
<wire x1="43.18" y1="15.24" x2="40.64" y2="15.24" width="0.1524" layer="91"/>
<label x="40.64" y="15.24" size="1.778" layer="95" rot="R180" xref="yes"/>
</segment>
</net>
<net name="+9V" class="0">
<segment>
<pinref part="J1" gate="G$1" pin="2"/>
<wire x1="76.2" y1="35.56" x2="76.2" y2="33.02" width="0.1524" layer="91"/>
<label x="76.2" y="33.02" size="1.778" layer="95" rot="R270" xref="yes"/>
</segment>
</net>
<net name="UART_5V" class="0">
<segment>
<pinref part="J1" gate="G$1" pin="3"/>
<wire x1="78.74" y1="35.56" x2="78.74" y2="33.02" width="0.1524" layer="91"/>
<label x="78.74" y="33.02" size="1.778" layer="95" rot="R270" xref="yes"/>
</segment>
<segment>
<pinref part="SV2" gate="1" pin="6"/>
<wire x1="43.18" y1="12.7" x2="40.64" y2="12.7" width="0.1524" layer="91"/>
<label x="40.64" y="12.7" size="1.778" layer="95" rot="R180" xref="yes"/>
</segment>
</net>
<net name="+5V" class="0">
<segment>
<wire x1="81.28" y1="81.28" x2="81.28" y2="86.36" width="0.1524" layer="91"/>
<label x="81.28" y="86.36" size="1.778" layer="95" rot="R90" xref="yes"/>
<pinref part="JP26" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="81.28" y1="71.12" x2="81.28" y2="66.04" width="0.1524" layer="91"/>
<label x="81.28" y="66.04" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP32" gate="G$1" pin="1"/>
</segment>
<segment>
<pinref part="SV2" gate="1" pin="3"/>
<wire x1="43.18" y1="20.32" x2="40.64" y2="20.32" width="0.1524" layer="91"/>
<label x="40.64" y="20.32" size="1.778" layer="95" rot="R180" xref="yes"/>
</segment>
</net>
<net name="+3V" class="0">
<segment>
<wire x1="78.74" y1="81.28" x2="78.74" y2="86.36" width="0.1524" layer="91"/>
<label x="78.74" y="86.36" size="1.778" layer="95" rot="R90" xref="yes"/>
<pinref part="JP27" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="78.74" y1="71.12" x2="78.74" y2="66.04" width="0.1524" layer="91"/>
<label x="78.74" y="66.04" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP33" gate="G$1" pin="1"/>
</segment>
<segment>
<pinref part="SV1" gate="1" pin="3"/>
<wire x1="7.62" y1="17.78" x2="10.16" y2="17.78" width="0.1524" layer="91"/>
<label x="10.16" y="17.78" size="1.778" layer="95" xref="yes"/>
</segment>
</net>
<net name="PC6" class="0">
<segment>
<wire x1="25.4" y1="81.28" x2="25.4" y2="86.36" width="0.1524" layer="91"/>
<label x="25.4" y="86.36" size="1.778" layer="95" rot="R90" xref="yes"/>
<pinref part="JP2" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="50.8" y1="71.12" x2="50.8" y2="66.04" width="0.1524" layer="91"/>
<label x="50.8" y="66.04" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP14" gate="G$1" pin="1"/>
</segment>
</net>
<net name="PC7" class="0">
<segment>
<wire x1="25.4" y1="71.12" x2="25.4" y2="66.04" width="0.1524" layer="91"/>
<label x="25.4" y="66.04" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP4" gate="G$1" pin="1"/>
</segment>
<segment>
<wire x1="35.56" y1="71.12" x2="35.56" y2="66.04" width="0.1524" layer="91"/>
<label x="35.56" y="66.04" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP8" gate="G$1" pin="1"/>
</segment>
</net>
<net name="UART_3V" class="0">
<segment>
<wire x1="68.58" y1="116.84" x2="68.58" y2="111.76" width="0.1524" layer="91"/>
<label x="68.58" y="111.76" size="1.778" layer="95" rot="R270" xref="yes"/>
<pinref part="JP43" gate="G$1" pin="1"/>
</segment>
<segment>
<pinref part="SV1" gate="1" pin="6"/>
<wire x1="7.62" y1="25.4" x2="10.16" y2="25.4" width="0.1524" layer="91"/>
<label x="10.16" y="25.4" size="1.778" layer="95" xref="yes"/>
</segment>
</net>
<net name="LV1" class="0">
<segment>
<pinref part="SV1" gate="1" pin="1"/>
<wire x1="7.62" y1="12.7" x2="10.16" y2="12.7" width="0.1524" layer="91"/>
<label x="10.16" y="12.7" size="1.778" layer="95" xref="yes"/>
</segment>
</net>
<net name="HV1" class="0">
<segment>
<pinref part="SV2" gate="1" pin="1"/>
<wire x1="43.18" y1="25.4" x2="40.64" y2="25.4" width="0.1524" layer="91"/>
<label x="40.64" y="25.4" size="1.778" layer="95" rot="R180" xref="yes"/>
</segment>
</net>
</nets>
</sheet>
</sheets>
</schematic>
</drawing>
</eagle>
