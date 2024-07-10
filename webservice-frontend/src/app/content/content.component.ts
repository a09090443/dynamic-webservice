import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {JsonPipe} from "@angular/common";

@Component({
  selector: 'app-content',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    JsonPipe
  ],
  templateUrl: './content.component.html',
  styleUrl: './content.component.css'
})
export class ContentComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { content: string }) {}

  get formattedContent() {
    const content = this.data.content.trim();
    if (this.isJson(content)) {
      return this.formatJson(content);
    } else if (this.isXml(content)) {
      return this.formatXml(content);
    } else {
      return content;
    }
  }

  private isJson(str: string): boolean {
    try {
      JSON.parse(str);
      return true;
    } catch (e) {
      return false;
    }
  }

  private isXml(str: string): boolean {
    try {
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(str, "text/xml");
      const errorNode = xmlDoc.querySelector("parsererror");
      return !errorNode;
    } catch (e) {
      return false;
    }
  }

  private formatJson(json: string): string {
    try {
      const obj = JSON.parse(json);
      return JSON.stringify(obj, null, 2);
    } catch (e) {
      return json;
    }
  }

  private formatXml(xml: string): string {
    try {
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(xml, 'text/xml');
      return this.formatXmlNode(xmlDoc.documentElement, 0);
    } catch (e) {
      return xml;
    }
  }

  private formatXmlNode(node: Element, level: number): string {
    let result = '';
    const indent = '  '.repeat(level);

    // Start tag
    result += `${indent}<${this.getFullTagName(node)}`;

    // Attributes
    for (let i = 0; i < node.attributes.length; i++) {
      const attr = node.attributes[i];
      result += ` ${attr.name}="${attr.value}"`;
    }

    if (node.childNodes.length === 0) {
      result += '/>\n';
    } else {
      result += '>\n';

      // Child nodes
      for (let i = 0; i < node.childNodes.length; i++) {
        const child = node.childNodes[i];
        if (child.nodeType === Node.ELEMENT_NODE) {
          result += this.formatXmlNode(child as Element, level + 1);
        } else if (child.nodeType === Node.TEXT_NODE) {
          const text = child.textContent?.trim();
          if (text) {
            result += `${indent}  ${text}\n`;
          }
        }
      }

      // End tag
      result += `${indent}</${this.getFullTagName(node)}>\n`;
    }

    return result;
  }

  private getFullTagName(node: Element): string {
    return node.prefix ? `${node.prefix}:${node.localName}` : node.tagName;
  }

  get formattedJson() {
    try {
      const jsonObject = JSON.parse(this.data.content);
      return JSON.stringify(jsonObject, null, 2);
    } catch (e) {
      return this.data.content;
    }
  }
}
