package org.pvoid.apteryxaustralis.net;

import org.xml.sax.Attributes;

public interface IResponseParser
{
  void SectionStart();
  void SectionEnd();
  void ElementStart(String name, Attributes attributes); 
  void ElementEnd(String innerText);
}
